package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel.Type;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class BanCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null && author.getId().equals(Owner.USER)) {
      if (message.split(" ").length != 2) {
        return false;
      }
      return (message.toLowerCase().startsWith("ban"));
    }

    return false;
  }

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    Snowflake userId = Snowflake.of(message.split(" ", 2)[1]);

    if (userId.equals(Owner.USER)) {
      Owner.sendMessage("Error:", "You cannot ban yourself!");
      return;
    }

    if (BotUtils.isBanned(userId.asString())) {
      Owner.sendMessage("Error:", BotUtils.CLIENT.getUserById(userId).block()
          .getUsername() + " is already banned.");
      return;
    }

    try {
      Writer output;
      output = new BufferedWriter(new FileWriter("banned.txt", true));

      output.append(userId.asString());
      output.append('\n');

      output.close();

      Owner.sendMessage("Ban Successful:", BotUtils.CLIENT.getUserById(userId).block()
          .getUsername() + " is now banned.");

    } catch (Exception e) {
      e.printStackTrace();
      Owner.sendMessage("Error Banning:", e.getMessage());
    }
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.USER)) {
      return "`ban <Discord ID>` - Bans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
