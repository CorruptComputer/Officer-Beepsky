package xyz.gupton.nickolas.beepsky.music.commands;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

/**
 * Command to stop the currently playing queue.
 */
public class StopCommand implements Command {

  /**
   * Checks that the command was sent in a Guild, the command is correct, and that the bot is
   * currently in a VoiceChannel.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null) {
      return false;
    }

    if (message.equalsIgnoreCase(BotUtils.getInstance().PREFIX + "stop")
        || message.equalsIgnoreCase(BotUtils.getInstance().PREFIX + "clear")) {

      // if the bot is not in a voice channel ignore the commands
      Member self = guild.getMemberById(BotUtils.getInstance().GATEWAY.getSelfId()).block();
      if (self == null) {
        return false;
      }

      VoiceState selfVoiceState = self.getVoiceState().block();
      if (selfVoiceState == null) {
        return false;
      }

      VoiceChannel selfVoiceChannel = selfVoiceState.getChannel().block();
      return selfVoiceChannel != null;
    }

    return false;
  }

  /**
   * Clears the queue for the current Guild.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    GuildMusicManager musicManager = MusicHelper.getGuildMusicManager(guild.getId());
    MusicHelper.clearQueue(musicManager.getScheduler());
    BotUtils.getInstance().sendMessage(channel, author, "The queue has been cleared!", "", Color.GREEN);
  }

  /**
   * Returns the usage string for the StopCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.getInstance().PREFIX + "stop` or `"
        + BotUtils.getInstance().PREFIX + "clear` - Clears the current queue and leaves the voice channel.";
  }
}
