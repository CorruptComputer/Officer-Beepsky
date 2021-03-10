package xyz.gupton.nickolas.beepsky.general.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.PrivateChannel;
import discord4j.rest.util.Color;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class HelpCommand implements Command {

  /**
   * Checks the message for the correct command.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    return message.toLowerCase().equals(BotUtils.PREFIX + "help");
  }

  /**
   * PM's the user all available commands.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    StringBuilder commandStr = new StringBuilder();

    PrivateChannel privateChannel = author.getPrivateChannel().block();
    if (privateChannel == null) {
      return;
    }

    for (Command commands : BotUtils.commands) {
      String cmd = commands.getCommand(author);
      if (cmd.length() > 0) {
        commandStr.append(cmd);
        commandStr.append("\n\n");
      }

      if (commandStr.length() > 1800) {
        BotUtils.sendMessage(privateChannel, author, "Available Commands:",
            commandStr.toString(), Color.ORANGE);
        commandStr.delete(0, commandStr.length());
      }
    }
    commandStr.append(
        "Officer-Beepsky is an open source Discord bot, you can view the source here on [GitHub](https://github.com/CorruptComputer/Officer-Beepsky).");
    BotUtils.sendMessage(privateChannel, author, "Available Commands:",
        commandStr.toString(), Color.ORANGE);
  }

  /**
   * Returns the usage string for the HelpCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "help` - You should already know this one.";
  }
}
