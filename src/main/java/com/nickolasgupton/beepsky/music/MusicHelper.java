package com.nickolasgupton.beepsky.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import java.util.HashMap;
import java.util.Map;
import sx.blah.discord.handle.obj.IGuild;

class MusicHelper {

  static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
  private static final Map<Long, GuildMusicManager> playerInstances = new HashMap<>();

  /**
   * Returns the provided Guilds audio player.
   *
   * @param guild The guild to get the Audio Player of
   * @return GuildMusicManager for the provided Guild
   */
  static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
    long guildId = guild.getLongID();

    GuildMusicManager musicManager = playerInstances.computeIfAbsent(guildId,
        manager -> new GuildMusicManager(playerManager));

    guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

    return musicManager;
  }
}
