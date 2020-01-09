package xyz.gupton.nickolas.beepsky.admin.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.Globals;
import xyz.gupton.nickolas.beepsky.configuration.Bot;

public class BanCommand implements Command {

  /**
   * Checks the command and if it was sent in a PM to the bot.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null && author.getId().toString().equals(Globals.CONFIG.getOwner())) {
      if (message.split(" ").length != 2) {
        return false;
      }
      return (message.toLowerCase().startsWith("ban"));
    }

    return false;
  }

  /**
   * Bans the specified user from using the bot.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    Snowflake userId = Snowflake.of(message.split(" ", 2)[1]);

    if (userId.toString().equals(Globals.CONFIG.getOwner())) {
      BotUtils.sendOwnerMessage("Error:", "You cannot ban yourself!");
      return;
    }

    if (BotUtils.isBanned(userId.asString())) {
      BotUtils.sendOwnerMessage("Error:", Globals.CLIENT.getUserById(userId).block()
          .getUsername() + " is already banned.");
      return;
    }

    try {
      Writer output;
      output = new BufferedWriter(new FileWriter("banned.txt", true));

      output.append(userId.asString());
      output.append('\n');

      output.close();

      BotUtils.sendOwnerMessage("Ban Successful:", Globals.CLIENT.getUserById(userId).block()
          .getUsername() + " is now banned.");

    } catch (Exception e) {
      e.printStackTrace();
      BotUtils.sendOwnerMessage("Error Banning:", e.getMessage());
    }
  }

  /**
   * Returns the usage string for the BanCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  public String getCommand(User recipient) {
    if (recipient.getId().toString().equals(Globals.CONFIG.getOwner())) {
      return "`ban <Discord ID>` - Bans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
