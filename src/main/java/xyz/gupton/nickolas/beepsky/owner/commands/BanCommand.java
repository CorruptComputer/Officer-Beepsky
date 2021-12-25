package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

/**
 * Command for banning people from using the bot.
 */
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
    if (guild == null && author.getId().equals(Owner.OWNER_USER)) {
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

    if (userId.equals(Owner.OWNER_USER)) {
      Owner.sendMessage("Error:", "You cannot ban yourself!");
      return;
    }

    User bannedUser = BotUtils.GATEWAY.getUserById(userId).block();
    String username = bannedUser != null ? bannedUser.getUsername() : "'Unknown User'";

    if (BotUtils.isBanned(userId.asString())) {
      Owner.sendMessage("Error:", username + " is already banned.");
      return;
    }

    try (Writer output = new BufferedWriter(new FileWriter("banned.txt", true))) {
      output.append(userId.asString());
      output.append('\n');
      Owner.sendMessage("Ban Successful:", username + " is now banned.");
    } catch (Exception e) {
      e.printStackTrace();
      Owner.sendMessage("Error Banning:", e.getMessage());
    }
  }

  /**
   * Returns the usage string for the BanCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.OWNER_USER)) {
      return "`ban <Discord ID>` - Bans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
