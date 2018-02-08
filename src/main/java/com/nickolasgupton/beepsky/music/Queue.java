package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.MusicHelper.getGuildAudioPlayer;

import com.nickolasgupton.beepsky.BotUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

public class Queue {

  enum QueueReturnCode {
    NOT_IN_VOICE,
    ALREADY_IN_USE,
    SUCCESS
  }

  /**
   * Loads and plays the song specified.
   * @param author Requester of the song.
   * @param channel Text channel it was requested in
   * @param track Track to play.
   */
  static QueueReturnCode addToQueue(IUser author, IChannel channel, final String track) {
    IVoiceChannel userVoiceChannel = author.getVoiceStateForGuild(channel.getGuild()).getChannel();

    // user is not in a voice channel
    if (userVoiceChannel == null) {
      return QueueReturnCode.NOT_IN_VOICE;
    }

    IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
        .getVoiceStateForGuild(channel.getGuild()).getChannel();

    // if the bot is not currently in a voice channel, join the user
    if (botVoiceChannel == null) {
      userVoiceChannel.join();
    } else {
      // if the bot is currently in a voice channel that isn't the one that the user in in
      if (botVoiceChannel != userVoiceChannel) {
        return QueueReturnCode.ALREADY_IN_USE;
      }
    }

    AudioSourceManagers.registerRemoteSources(MusicHelper.playerManager);
    AudioSourceManagers.registerLocalSource(MusicHelper.playerManager);
    GuildMusicManager musicManager = MusicHelper.getGuildAudioPlayer(channel.getGuild());

    EmbedBuilder builder = new EmbedBuilder();
    builder.withFooterText(author.getDisplayName(channel.getGuild()));
    builder.withColor(100, 255, 100);

    MusicHelper.playerManager
        .loadItemOrdered(musicManager, track, new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            builder.withTitle("Adding to queue:");
            builder.withDescription("[" + track.getInfo().title + "](" + track + ")" + " by "
                + track.getInfo().author);

            BotUtils.sendMessage(channel, builder.build());
            musicManager.getScheduler().queue(track);
          }

          @Override
          public void playlistLoaded(AudioPlaylist playlist) {
            // if it is a search vs an actual playlist
            if (track.startsWith("ytsearch:") || track.startsWith("scsearch:")) {

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
                  .queueToString(MusicHelper.getGuildAudioPlayer(channel.getGuild()).getScheduler()
                      .getQueue());

              // message with the first song
              builder.withTitle("Adding playlist to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "**First track:** " + "[" + firstTrack.getInfo().title + "]("
                  + firstTrack.getInfo().uri + ")\n\n"
                  + "**Next up:**\n" + str);
            }

            BotUtils.sendMessage(channel, builder.build());
          }

          @Override
          public void noMatches() {
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Nothing found at: " + track);

            BotUtils.sendMessage(channel, builder.build());
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Could not play track: " + exception.getMessage());

            BotUtils.sendMessage(channel, builder.build());
          }
        });

    return QueueReturnCode.SUCCESS;
  }

  /**
   * Skips the currently playing song and starts the next one.
   * @param event Provided by D4J.
   */
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
    BotUtils.sendMessage(event.getChannel(), builder.build());

    event.getMessage().delete();
  }

  /**
   * Lists the currently playing queue.
   * @param event Provided by D4J.
   */
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
      BotUtils.sendMessage(event.getChannel(), builder.build());
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

  /**
   * Disconnect from voice channel and clear the queue of all songs.
   * @param event Provided by D4J.
   */
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

  /**
   * Clears the queue for the current guild.
   * @param scheduler TrackScheduler for the current guild.
   */
  static void clear(TrackScheduler scheduler) {
    scheduler.getQueue().clear();
    scheduler.nextTrack();
  }
}
