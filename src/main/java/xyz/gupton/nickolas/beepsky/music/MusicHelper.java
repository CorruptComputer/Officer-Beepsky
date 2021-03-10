package xyz.gupton.nickolas.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.common.util.Snowflake;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHelper {

  public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  private static final Map<Long, GuildMusicManager> playerInstances = new HashMap<>();

  /**
   * Returns the provided Guilds music manager.
   *
   * @param guild The guild to get the music manager of
   * @return GuildMusicManager for the provided Guild
   */
  public static synchronized GuildMusicManager getGuildMusicManager(Snowflake guild) {
    return playerInstances.computeIfAbsent(guild.asLong(),
        manager -> new GuildMusicManager(playerManager, guild));
  }

  /**
   * Formats the currently queued songs for output.
   *
   * @param queue List of the AudioTracks currently queued
   * @return Returns formatted String of the songs
   */
  public static String queueToString(List<AudioTrack> queue) {

    StringBuilder str = new StringBuilder();

    for (int i = 0; i < queue.size(); i++) {

      // I hate the way this looks, but Intellij says its faster than string concatenation
      str.append((i + 1)).append(". [").append(queue.get(i).getInfo().title)
          .append("](").append(queue.get(i).getInfo().uri)
          .append(") by ").append(queue.get(i).getInfo().author).append("\n");

      // discord has a character limit of 2048, ~250 extra characters for the rest of the message
      if (i == 24 || str.length() > 1800) {
        str.append("+ ").append((queue.size() - i)).append(" more songs.");
        break;
      }
    }

    if (str.toString().equals("")) {
      str.append("Nothing currently queued.");
    } else {
      str.insert(0, "Next up:\n");
    }

    return str.toString();
  }

  /**
   * Clears the queue for the current guild.
   *
   * @param scheduler TrackScheduler for the current guild.
   */
  public static void clearQueue(TrackScheduler scheduler) {
    scheduler.getQueue().clear();
    scheduler.nextTrack();
  }
}
