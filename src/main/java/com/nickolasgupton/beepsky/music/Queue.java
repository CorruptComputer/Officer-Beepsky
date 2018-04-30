package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.MusicHelper.getGuildAudioPlayer;

import com.nickolasgupton.beepsky.BotUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.util.List;
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
   * @param textChannel Text channel it was requested in
   * @param track Track to play.
   *
   * @return Returns the status of the addition, can either be NOT_IN_VOICE which means the
   *     requester is not in a voice channel, ALREADY_IN_USE which means the bot is being used in
   *     another voice channel for that guild, or SUCCESS which means everything went smoothly.
   */
  static QueueReturnCode addToQueue(IUser author, IChannel textChannel, final String track) {
    IVoiceChannel userVoiceChannel =
        author.getVoiceStateForGuild(textChannel.getGuild()).getChannel();

    // user is not in a voice channel
    if (userVoiceChannel == null) {
      return QueueReturnCode.NOT_IN_VOICE;
    }

    IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
        .getVoiceStateForGuild(textChannel.getGuild()).getChannel();

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
    GuildMusicManager musicManager = MusicHelper.getGuildAudioPlayer(textChannel.getGuild());

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);

    MusicHelper.playerManager
        .loadItemOrdered(musicManager, track, new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            builder.withTitle("Adding to queue:");
            builder.withDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                + " by " + track.getInfo().author);

            BotUtils.sendMessage(textChannel, author, builder);
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
                  .queueToString(MusicHelper.getGuildAudioPlayer(textChannel.getGuild())
                      .getScheduler().getQueue());

              // message with the first song
              builder.withTitle("Adding playlist to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "**First track:** " + "[" + firstTrack.getInfo().title + "]("
                  + firstTrack.getInfo().uri + ")\n\n"
                  + "**Next up:**\n" + str);
            }

            BotUtils.sendMessage(textChannel, author, builder);
          }

          @Override
          public void noMatches() {
            builder.withColor(Color.red);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Nothing found at: " + track);

            BotUtils.sendMessage(textChannel, author, builder);
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            builder.withColor(Color.red);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Could not play track: " + exception.getMessage());

            BotUtils.sendMessage(textChannel, author, builder);
          }
        });

    return QueueReturnCode.SUCCESS;
  }

  /**
   * Skips the currently playing song and starts the next one.
   * @param author Requester of skip
   * @param textChannel Text channel it was requested in
   */
  static void nextSong(IUser author, IChannel textChannel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());
    List<AudioTrack> queue = musicManager.getScheduler().getQueue();
    EmbedBuilder builder = new EmbedBuilder();

    builder.withColor(Color.green);

    if (queue.size() > 0) {
      builder.withTitle("Skipped to next track, now playing:");
      builder.withDescription("[" + queue.get(0).getInfo().title + "](" + queue.get(0).getInfo().uri
          + ")" + " by " + queue.get(0).getInfo().author);
    } else {
      builder.withTitle("Skipped to next track, nothing left to play.");
    }

    musicManager.getScheduler().nextTrack();
    BotUtils.sendMessage(textChannel, author, builder);
  }

  /**
   * Lists the currently playing queue.
   * @param author Requester of the queue
   * @param textChannel Text channel it was requested in
   */
  static void listQueue(IUser author, IChannel textChannel) {
    GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

    String str = queueToString(musicManager.getScheduler().getQueue());

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);
    builder.withTitle("Next up:");
    builder.withDescription(str);

    int len = builder.getTotalVisibleCharacters();
    if (len > 2048) {
      builder.withColor(Color.red);
      builder.withTitle("Error listing queue:");
      builder.withDescription("Too long! Length: " + len);
    } else {
      BotUtils.sendMessage(textChannel, author, builder);
    }
  }

  /**
   * Formats the currently queued songs for output.
   *
   * @param queue List of the AudioTracks currently queued
   * @return Returns formatted String of the songs
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
   * @param author Requester of the full stop
   * @param textChannel Text channel it was requested in
   */
  static void stop(IUser author, IChannel textChannel) {
    IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
        .getVoiceStateForGuild(textChannel.getGuild()).getChannel();

    if (botVoiceChannel == null) {
      return;
    }

    clear(getGuildAudioPlayer(textChannel.getGuild()).getScheduler());
    EmbedBuilder message = new EmbedBuilder();
    message.withColor(Color.green);
    message.withTitle("The queue has been cleared!");

    BotUtils.sendMessage(textChannel, author, message);
    botVoiceChannel.leave();
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
