package com.nickolasgupton.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

  private final List<AudioTrack> queue;
  private final AudioPlayer player;

  TrackScheduler(AudioPlayer player) {
    // Because we will be removing from the "head" of the queue frequently, a LinkedList is a better
    // implementation since all elements won't have to be shifted after removing. Additionally,
    // choosing to add in between the queue will similarly not cause many elements to shift and wil
    // only require a couple of node changes.
    queue = Collections.synchronizedList(new LinkedList<>());
    this.player = player;

    // For encapsulation, keep the listener anonymous.
    player.addListener(new AudioEventAdapter() {
      @Override
      public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
          nextTrack();
        }
      }
    });
  }

  /**
   * Add the next track to queue or play right away if nothing is in the queue.
   *
   * @param track The track to play or add to queue.
   * @return Returns false and does nothing if something is already playing
   */
  public synchronized boolean queue(AudioTrack track) {
    // Calling startTrack with the noInterrupt set to true will start the track only if nothing is
    // currently playing. If something is playing, it returns false and does nothing. In that case
    // the player was already playing so this track goes to the queue instead.
    boolean playing = player.startTrack(track, true);

    if (!playing) {
      queue.add(track);
    }

    return playing;
  }

  /**
   * Starts the next track, stopping the current one if it is playing.
   *
   * @return The track that was stopped, null if there wasn't anything playing
   */
  public synchronized AudioTrack nextTrack() {
    AudioTrack currentTrack = player.getPlayingTrack();
    AudioTrack nextTrack = queue.isEmpty() ? null : queue.remove(0);

    // Start the next track, regardless of if something is already playing or not. In case queue was
    // empty, we are  giving null to startTrack, which is a valid argument and will simply stop the
    // player.
    player.startTrack(nextTrack, false);
    return currentTrack;
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

  @Override
  public void onPlayerPause(AudioPlayer player) {
    // Player was paused
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    // Player was resumed
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    // A track started playing
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    if (endReason.mayStartNext) {
      // Start next track
    }

    // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
    // endReason == STOPPED: The player was stopped.
    // endReason == REPLACED: Another track started playing while this had not finished
    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
    //                       clone of this back to your queue
  }

  @Override
  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
    // Audio track has been unable to provide us any audio, might want to just start a new track
  }
}
