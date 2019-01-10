package xyz.gupton.nickolas.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import xyz.gupton.nickolas.beepsky.BotUtils;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {

  private final List<AudioTrack> queue;
  private final AudioPlayer player;
  public IGuild guild;

  /**
   * Constructor for TrackScheduler.
   *
   * @param player AudioPlayer for the current guild.
   */
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

        GuildMusicManager musicManager = MusicHelper.getGuildMusicManager(guild);

        if (musicManager.getScheduler().getPlayingSong() == null
            && musicManager.getScheduler().getQueue().isEmpty()) {
          MusicHelper.clearQueue(musicManager.getScheduler());

          nextTrack();

          IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
              .getVoiceStateForGuild(guild).getChannel();

          botVoiceChannel.leave();
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

    if (!playing) {
      queue.add(track);
    }

  }

  /**
   * Starts the next track, stopping the current one if it is playing.
   */
  public synchronized void nextTrack() {
    AudioTrack nextTrack = queue.isEmpty() ? null : queue.remove(0);

    // Start the next track, regardless of if something is already playing or not. In case queue was
    // empty, we are giving null to startTrack, which is a valid argument and will simply stop the
    // player.
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
