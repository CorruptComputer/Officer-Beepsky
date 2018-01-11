package com.nickolasgupton.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {

  private final AudioPlayer player;
  private final AudioProvider provider;
  private final TrackScheduler scheduler;

  /**
   * Creates a player and a track scheduler.
   *
   * @param manager Audio player manager to use for creating the player.
   */
  GuildMusicManager(AudioPlayerManager manager) {
    player = manager.createPlayer();
    provider = new AudioProvider(player);
    scheduler = new TrackScheduler(player);
  }

  /**
   * Adds a listener to be registered for audio events.
   * @param listener Listener to be added
   */
  public void addAudioListener(AudioEventListener listener) {
    player.addListener(listener);
  }

  /**
   * Removes a listener that was registered for audio events.
   * @param listener Listener to be removed
   */
  public void removeAudioListener(AudioEventListener listener) {
    player.removeListener(listener);
  }

  /**
   * Gets the Scheduler for the current guild.
   *
   * @return The scheduler for AudioTracks.
   */
  public TrackScheduler getScheduler() {
    return this.scheduler;
  }

  /**
   * Gets the Audio Provider for the current guild.
   *
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public AudioProvider getAudioProvider() {
    return provider;
  }
}

