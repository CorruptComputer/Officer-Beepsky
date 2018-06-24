package com.nickolasgupton.beepsky.general;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.util.ServiceLoader;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a command should be executed.
   *
   * @param message The message received.
   * @return True if the command should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    return message.toString().toLowerCase().equals(BotUtils.PREFIX + "help");
  }

  /**
   * Executes the command if it exists.
   *
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("Available Commands:");
    builder.withDescription("");
    for (Command commands : ServiceLoader.load(Command.class)) {
      String cmd = commands.getCommand(event.getAuthor());
      if (cmd.length() > 0) {
        builder.appendDescription(commands.getCommand(event.getAuthor()) + "\n\n");
      }

      if (builder.getTotalVisibleCharacters() > 1800) {
        BotUtils.sendMessage(event.getAuthor().getOrCreatePMChannel(), event.getAuthor(), builder);
        builder.withDescription("");
      }
    }
    builder.appendDescription("Officer-Beepsky is an open source Discord bot, you can view the source here on [GitHub](https://github.com/CorruptComputer/Officer-Beepsky).");
    BotUtils.sendMessage(event.getAuthor().getOrCreatePMChannel(), event.getAuthor(), builder);
  }

  /**
   * Returns the usage string for a command.
   *
   * @return String of the correct usage for the command.
   */
  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "help` - You should already know this one.";
  }
}
