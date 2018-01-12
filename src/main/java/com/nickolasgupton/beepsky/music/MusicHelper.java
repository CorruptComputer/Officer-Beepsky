package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.Queue.addToQueue;

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

  static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  static final Map<Long, GuildMusicManager> playerInstances = new HashMap<>();

  /**
   * Returns the provided Guilds audio player.
   *
   * @param guild The guild to get the Audio Player of
   * @return GuildMusicManager for the provided Guild
   */
  static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
    long guildId = guild.getLongID();
    GuildMusicManager musicManager = playerInstances.get(guildId);

    if (musicManager == null) {
      musicManager = new GuildMusicManager(playerManager);
      playerInstances.put(guildId, musicManager);
    }

    guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

    return musicManager;
  }



}
