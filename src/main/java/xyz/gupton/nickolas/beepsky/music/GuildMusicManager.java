package xyz.gupton.nickolas.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import discord4j.core.object.util.Snowflake;
import discord4j.voice.VoiceConnection;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {

  private final AudioPlayer player;
  private final D4jAudioProvider provider;
  private final TrackScheduler scheduler;
  private VoiceConnection botVoiceConnection = null;

  /**
   * Creates a player and a track scheduler.
   *
   * @param manager Audio player manager to use for creating the player.
   * @param guild Guild the manager is being used for.
   */
  GuildMusicManager(AudioPlayerManager manager, Snowflake guild) {
    this.player = manager.createPlayer();
    this.provider = new D4jAudioProvider(player);
    this.scheduler = new TrackScheduler(player, guild);
  }

  /**
   * Adds a listener to be registered for audio events.
   *
   * @param listener Listener to be added
   */
  public void addAudioListener(AudioEventListener listener) {
    player.addListener(listener);
  }

  /**
   * Removes a listener that was registered for audio events.
   *
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
   * Gets the AudioPlayer for the current guild.
   *
   * @return The AudioPlayer for the guild.
   */
  public AudioPlayer getAudioPlayer() {
    return this.player;
  }

  /**
   * Gets the Audio Provider for the current guild.
   *
   * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
   */
  public D4jAudioProvider getAudioProvider() {
    return provider;
  }

  VoiceConnection getBotVoiceConnection() {
    return botVoiceConnection;
  }

  public void setBotVoiceConnection(VoiceConnection connection) {
    this.botVoiceConnection = connection;
  }
}

