package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class BanCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a command should be executed.
   *
   * @param message The message received.
   * @return True if the command should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      if (message.toString().split(" ").length != 2) {
        return false;
      }
      return (message.toString().toLowerCase().startsWith("ban"));
    }

    return false;
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

    Long userId = Long.parseUnsignedLong(event.getMessage().toString().split(" ", 2)[1]);

    if (userId == Owner.user.getLongID()) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error Banning");
      builder.withDescription("You cannot ban yourself!");
      Owner.sendMessage(builder);
      return;
    }

    if (BotUtils.isBanned(userId.toString())) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error Banning");
      builder.withDescription(BotUtils.CLIENT.getUserByID(userId).getName()
          + " is already banned.");
      Owner.sendMessage(builder);
      return;
    }

    try {
      Writer output;
      output = new BufferedWriter(new FileWriter("banned.txt", true));

      output.append(userId.toString());
      output.append('\n');

      output.close();

      builder.withTitle("Ban Successful");
      builder.withDescription(BotUtils.CLIENT.getUserByID(userId).getName()
              + " is now banned.");
    } catch (Exception e) {
      builder.withColor(255, 0, 0);
      e.printStackTrace();
      builder.withTitle("Error Banning");
      builder.withDescription("Error: " + e.getMessage());
    }

    Owner.sendMessage(builder);
  }

  /**
   * Returns the usage string for a command.
   *
   * @return String of the correct usage for the command.
   */
  @Override
  public String getCommand(IUser recipient) {
    if (recipient == Owner.user) {
      return "`ban <Discord ID>` - Bans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
