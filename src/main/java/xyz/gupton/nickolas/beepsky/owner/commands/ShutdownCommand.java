package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel.Type;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class ShutdownCommand implements Command {

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
      return (message.toLowerCase().equals("shutdown")
          || message.toLowerCase().equals("restart")
          || message.toLowerCase().equals("reboot"));
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

    boolean restart = !message.toLowerCase().equals("shutdown");

    if (restart) {
      Owner.sendMessage("Restarting...",
          "This may take up to a few minutes if an update is available.");
    } else {
      Owner.sendMessage("Shutting down...", "Goodbye world.");
    }

    BotUtils.CLIENT.logout().block();
    System.exit(restart ? 1 : 0);
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
      return "__**The following commands must be used in a PM, else they are ignored.**__\n\n"
          + "`shutdown` - Shuts the bot down.\n\n"
          + "`restart` or `reboot` "
          + "- Updates and restarts the bot. Only works if using the provided script.";
    }

    return "";
  }
}
