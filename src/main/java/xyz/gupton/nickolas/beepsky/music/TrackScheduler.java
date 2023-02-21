package xyz.gupton.nickolas.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import discord4j.common.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import xyz.gupton.nickolas.beepsky.EventHandlers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 * Per guild.
 */
public class TrackScheduler extends AudioEventAdapter {
  private final static Logger _logger = LoggerFactory.getLogger(TrackScheduler.class);
  private final List<AudioTrack> queue;
  private final AudioPlayer player;
  private final Snowflake _guild;

  /**
   * Constructor for TrackScheduler.
   *
   * @param player AudioPlayer for the current guild.
   * @param guild Snowflake ID of the current Guild.
   */
  TrackScheduler(AudioPlayer player, Snowflake guild) {
    queue = Collections.synchronizedList(new LinkedList<>());
    this.player = player;
    this._guild = guild;

    // For encapsulation, keep the listener anonymous.
    player.addListener(new AudioEventAdapter() {
      @Override
       public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        _logger.info("Guild: " + guild.asLong() + " Track ended: " + track.getInfo().title);

        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
          nextTrack();
        }

        GuildMusicManager musicManager = MusicHelper.getGuildMusicManager(guild);

        if (musicManager.getScheduler().getPlayingSong() == null
            && musicManager.getScheduler().getQueue().isEmpty()) {
          MusicHelper.clearQueue(musicManager.getScheduler());

          nextTrack();
          musicManager.disconnect();
        }
      }
    });
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   */
  public synchronized void queue(AudioTrack track) {
    // Calling startTrack with the noInterrupt set to true will start the track only if nothing is
    // currently playing. If something is playing, it returns false and does nothing. In that case
    // the player was already playing so this track goes to the queue instead.
    boolean playing = player.startTrack(track, true);

    _logger.info("Guild: " + _guild.asLong() + " Track added: " + track.getInfo().title);

    if (!playing) {
      queue.add(track);
    }
  }

  /**
   * Starts the next track, stopping the current one if it is playing.
   */
  public synchronized void nextTrack() {
    AudioTrack nextTrack = queue.isEmpty() ? null : queue.remove(0);

    String nextTrackTitle = nextTrack == null ? "No next track" : nextTrack.getInfo().title;
    _logger.info("Guild: " + _guild.asLong() + " Next track: " + nextTrackTitle);

    player.startTrack(nextTrack, false);
  }

  /**
   * Returns the queue for this scheduler. Adding to the head of the queue (index 0) does not
   * automatically cause it to start playing immediately. The returned collection is thread-safe and
   * can be modified.
   *
   * <p>To iterate over this queue, use a synchronized block. For example: {@code synchronize
   * (formatQueueOutput()) { // iteration code } }</p>
   *
   * @return Returns a List of AudioTrack's currently queued
   */
  public List<AudioTrack> getQueue() {
    return this.queue;
  }

  public AudioTrack getPlayingSong() {
    return player.getPlayingTrack();
  }
}