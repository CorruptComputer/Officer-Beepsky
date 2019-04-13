package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class LeaveCommand implements Command {

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

      return (message.toLowerCase().startsWith("leave"));
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
    BotUtils.CLIENT.getGuildById(Snowflake.of(message.split(" ", 2)[1])).block().leave().block();
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.USER)) {
      return "`leave <Server ID>` - Leaves that server.";
    }

    return "";
  }
}
