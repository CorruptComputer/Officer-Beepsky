package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.MusicHelper.getGuildAudioPlayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import java.util.regex.Pattern;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Queue {

  /**
   * Loads and plays the song specified.
   *
   * @param event Provided by D4J
   */
  static void addToQueue(MessageReceivedEvent event) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(255, 0, 0);
    builder.withTitle("Error queueing track:");
    builder.withFooterText(event.getAuthor().getDisplayName(event.getGuild()));

    // user only messages "!!queue" with no track data
    if (event.getMessage().getContent().split(" ").length == 1) {
      builder.withDescription("No track specified.");
      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
      event.getMessage().delete();
      return;
    }

    IVoiceChannel userVoiceChannel = event.getAuthor().getVoiceStateForGuild(event.getGuild())
        .getChannel();

    // user is not in a voice channel
    if (userVoiceChannel == null) {
      builder.withDescription("Not in a voice channel, join one and then try again.");
      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
      event.getMessage().delete();
      return;
    }

    IVoiceChannel botVoiceChannel = event.getClient().getOurUser()
        .getVoiceStateForGuild(event.getGuild()).getChannel();
    // if the bot is not currently in a voice channel, join the user
    if (botVoiceChannel == null) {
      // clear the queue before joining
      TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();

      scheduler.getQueue().clear();
      scheduler.nextTrack();

      userVoiceChannel.join();
    } else
      // if the bot is currently in a voice channel that isn't the one that the user in in
      if (botVoiceChannel != userVoiceChannel) {
        builder.withDescription(
            "Already in a voice channel, either join that channel or wait for them to finish.");
        RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
        event.getMessage().delete();
        return;
      }

    String[] split = event.getMessage().getContent().split(" ");
    String searchStr = "";

    for (int i = 1; i < split.length; i++) {
      searchStr += split[i] + " ";
    }

    // if it does not already contain a search keyword, and is a not a URL
    if (!(searchStr.startsWith("ytsearch:") || searchStr.startsWith("scsearch:"))
        // RegEx shamelessly copied from:
        // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
        && !Pattern
        .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        .matcher(searchStr).matches()) {
      searchStr = "ytsearch:" + searchStr;
    }

    AudioSourceManagers.registerRemoteSources(MusicHelper.playerManager);
    AudioSourceManagers.registerLocalSource(MusicHelper.playerManager);

    builder.withColor(100, 255, 100);
    builder.withFooterText(event.getAuthor().getDisplayName(event.getGuild()));

    GuildMusicManager musicManager = MusicHelper.getGuildAudioPlayer(event.getGuild());
    final String songToPlay = searchStr;
    MusicHelper.playerManager
        .loadItemOrdered(musicManager, songToPlay, new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            builder.withTitle("Adding to queue:");
            builder.withDescription("[" + track.getInfo().title + "](" + songToPlay + ")" + " by "
                + track.getInfo().author);

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            musicManager.getScheduler().queue(track);
          }

          @Override
          public void playlistLoaded(AudioPlaylist playlist) {
            // if it is a search vs an actual playlist
            if (songToPlay.startsWith("ytsearch:") || songToPlay.startsWith("scsearch:")) {

              builder.withTitle("Adding to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "[" + playlist.getTracks().get(0).getInfo().title + "]("
                  + playlist.getTracks().get(0).getInfo().uri + ")" + " by "
                  + playlist.getTracks().get(0).getInfo().author);
              musicManager.getScheduler().queue(playlist.getTracks().get(0));
            } else {
              AudioTrack firstTrack = playlist.getSelectedTrack();

              if (firstTrack == null) {
                firstTrack = playlist.getTracks().get(0);
              }

              musicManager.getScheduler().queue(firstTrack);

              // the queue for the playlist will start at the linked video
              for (int i = playlist.getTracks().indexOf(firstTrack) + 1;
                  i < playlist.getTracks().size(); i++) {
                musicManager.getScheduler().queue(playlist.getTracks().get(i));
              }

              String str = Queue
                  .queueToString(MusicHelper.getGuildAudioPlayer(event.getGuild()).getScheduler()
                      .getQueue());

              // message with the first song
              builder.withTitle("Adding playlist to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "**First track:** " + "[" + firstTrack.getInfo().title + "]("
                  + firstTrack.getInfo().uri + ")\n\n"
                  + "**Next up:**\n" + str);
            }

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            event.getMessage().delete();
          }

          @Override
          public void noMatches() {
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Nothing found at URL: " + songToPlay);

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Could not play track: " + exception.getMessage());

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
          }
        });
  }

  static void nextSong(MessageReceivedEvent event) {
    GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
    List<AudioTrack> queue = musicManager.getScheduler().getQueue();
    EmbedBuilder builder = new EmbedBuilder();

    builder.withColor(100, 255, 100);

    if (queue.size() > 0) {
      builder.withDescription("Skipped to next track, now playing:\n"
          + "[" + queue.get(0).getInfo().title + "](" + queue.get(0).getInfo().uri + ")" + " by "
          + queue.get(0).getInfo().author);
    } else {
      builder.withDescription("Skipped to next track, nothing left to play.");
    }

    builder.withFooterText(event.getAuthor().getDisplayName(event.getGuild()));

    musicManager.getScheduler().nextTrack();
    RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

    event.getMessage().delete();
  }

  static void listQueue(MessageReceivedEvent event) {
    GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());

    String str = queueToString(musicManager.getScheduler().getQueue());

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("Next up:");
    builder.withDescription(str);

    builder.withFooterText(event.getAuthor().getDisplayName(event.getGuild()));

    int len = builder.getTotalVisibleCharacters();
    if (len > 2048) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error listing queue:");
      builder.withDescription("Too long! Length: " + len);
    } else {
      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
    }
    event.getMessage().delete();
  }

  /**
   * Formats the currently queued songs for output.
   *
   * @param queue List of the AudioTracks currently queued
   * @return Returns Formatted String of the songs
   */
  private static String queueToString(List<AudioTrack> queue) {

    StringBuilder str = new StringBuilder();

    for (int i = 0; i < queue.size(); i++) {

      // I hate the way this looks, but Intellij says its faster than string concatenation
      str.append((i + 1)).append(". [").append(queue.get(i).getInfo().title)
          .append("](").append(queue.get(i).getInfo().uri)
          .append(") by ").append(queue.get(i).getInfo().author).append("\n");

      // discord has a character limit of 2048, ~300 extra for the rest of the message
      if (i == 19 || str.length() > 1700) {
        str.append("+ ").append((queue.size() - i)).append(" more songs.");
        break;
      }
    }

    if (str.toString().equals("")) {
      str.append("Nothing currently queued.");
    }

    return str.toString();
  }

  static void stop(MessageReceivedEvent event) {
    IVoiceChannel botVoiceChannel = event.getClient().getOurUser()
        .getVoiceStateForGuild(event.getGuild()).getChannel();

    if (botVoiceChannel == null) {
      return;
    }

    clear(getGuildAudioPlayer(event.getGuild()).getScheduler());

    botVoiceChannel.leave();

    event.getMessage().delete();
  }

  public static void clear(TrackScheduler scheduler) {
    scheduler.getQueue().clear();
    scheduler.nextTrack();
  }
}
