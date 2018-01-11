package com.nickolasgupton.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

// TODO: refactor this
public class MusicHelper {

  private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  private static final Map<Long, GuildMusicManager> playerInstances = new HashMap<>();

  /**
   * Returns the provided Guilds audio player.
   *
   * @param guild The guild to get the Audio Player of
   * @return GuildMusicManager for the provided Guild
   */
  public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
    long guildId = guild.getLongID();
    GuildMusicManager musicManager = playerInstances.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      playerInstances.put(guildId, musicManager);
    }

    guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

    return musicManager;
  }

  /**
   * Loads and plays the song specified.
   *
   * @param channel Channel to play the song in
   * @param songToPlay Can be a URL, or a search using "ytsearch:" or "scsearch:"
   * @param authorName Name of the user who requested the song
   */
  public static void loadAndPlay(final IChannel channel, final String songToPlay,
      final String authorName) {
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withFooterText(authorName);

    GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
    playerManager.loadItemOrdered(musicManager, songToPlay, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        builder.withTitle("Adding to queue:");
        builder.withDescription("[" + track.getInfo().title + "](" + songToPlay + ")" + " by "
            + track.getInfo().author);

        RequestBuffer.request(() -> channel.sendMessage(builder.build()));
        play(musicManager, track);
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
          play(musicManager, playlist.getTracks().get(0));
        } else {
          AudioTrack firstTrack = playlist.getSelectedTrack();

          if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
          }

          play(musicManager, firstTrack);

          // the queue for the playlist will start at the linked video
          for (int i = playlist.getTracks().indexOf(firstTrack) + 1;
              i < playlist.getTracks().size(); i++) {
            play(musicManager, playlist.getTracks().get(i));
          }

          String str = formatQueueOutput(getGuildAudioPlayer(channel.getGuild()).getScheduler()
              .getQueue());

          // message with the first song
          builder.withTitle("Adding playlist to queue:");
          builder.withDescription(playlist.getName() + "\n\n"
              + "**First track:** " + "[" + firstTrack.getInfo().title + "]("
              + firstTrack.getInfo().uri + ")\n\n"
              + "**Next up:**\n" + str);
        }

        RequestBuffer.request(() -> channel.sendMessage(builder.build()));
      }

      @Override
      public void noMatches() {
        builder.withColor(255, 0, 0);
        builder.withTitle("Error queueing track:");
        builder.withDescription("Nothing found at URL: " + songToPlay);

        RequestBuffer.request(() -> channel.sendMessage(builder.build()));
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        builder.withColor(255, 0, 0);
        builder.withTitle("Error queueing track:");
        builder.withDescription("Could not play track: " + exception.getMessage());

        RequestBuffer.request(() -> channel.sendMessage(builder.build()));
      }
    });
  }

  private static void play(GuildMusicManager musicManager, AudioTrack track) {
    musicManager.getScheduler().queue(track);
  }

  /**
   * Formats the currently queued songs for output.
   *
   * @param queue List of the AudioTracks currently queued
   * @return Returns Formatted String of the songs
   */
  public static String formatQueueOutput(List<AudioTrack> queue) {

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

    System.out.println(str.length());

    return str.toString();
  }

  public static void clearQueue(TrackScheduler scheduler) {
    scheduler.getQueue().clear();
    scheduler.nextTrack();
  }

}
