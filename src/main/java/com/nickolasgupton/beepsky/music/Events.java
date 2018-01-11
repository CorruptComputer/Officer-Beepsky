package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.MusicHelper.getGuildAudioPlayer;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class Events {

  /**
   * Called when a user disconnects from the current voice channel.
   *
   * @param event Provided by D4J
   */
  @EventSubscriber
  public void userLeftChannel(UserVoiceChannelLeaveEvent event) {
    // if the bot is the only one left in the channel
    if (event.getVoiceChannel().getConnectedUsers().size() <= 1) {
      IVoiceChannel botVoiceChannel = event.getClient().getOurUser()
          .getVoiceStateForGuild(event.getGuild()).getChannel();

      if (botVoiceChannel == null) {
        return;
      }

      MusicHelper.clearQueue(getGuildAudioPlayer(event.getGuild()).getScheduler());

      botVoiceChannel.leave();
    }
  }

  /**
   * Called when a user moves to another voice channel.
   *
   * @param event Provided by D4J
   */
  @EventSubscriber
  public void userMovedChannels(UserVoiceChannelMoveEvent event) {
    // if the bot is the only one left in the channel
    if (event.getOldChannel().getConnectedUsers().size() <= 1) {
      IVoiceChannel botVoiceChannel = event.getClient().getOurUser()
          .getVoiceStateForGuild(event.getGuild()).getChannel();

      if (botVoiceChannel == null) {
        return;
      }

      MusicHelper.clearQueue(getGuildAudioPlayer(event.getGuild()).getScheduler());

      botVoiceChannel.leave();
    }
  }
}
