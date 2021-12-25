package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

/**
 * Command to tell the bot to leave a server.
 */
public class LeaveCommand implements Command {

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

      return (message.toLowerCase().startsWith("leave"));
    }

    return false;
  }

  /**
   * Makes the bot leave the specified server.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    Guild guildToLeave = BotUtils.GATEWAY.getGuildById(
        Snowflake.of(message.split(" ", 2)[1])
    ).block();

    if (guildToLeave == null) {
      Owner.sendMessage("Error leaving server!", "Server ID does not exist.");
      return;
    }

    guildToLeave.leave().block();
    Owner.sendMessage("Left server!", "Successfully left server: " + guildToLeave.getName());
  }

  /**
   * Returns the usage string for the LeaveCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.OWNER_USER)) {
      return "`leave <Server ID>` - Leaves that server.";
    }

    return "";
  }
}
