package xyz.gupton.nickolas.beepsky.admin.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.Globals;

public class ShutdownCommand implements Command {

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
      return (message.toLowerCase().equals("shutdown")
          || message.toLowerCase().equals("restart")
          || message.toLowerCase().equals("reboot"));
    }

    return false;
  }

  /**
   * Shuts down or restarts the bot depending on the message.
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
      BotUtils.sendOwnerMessage("Restarting...",
          "This may take up to a few minutes if an update is available.");
    } else {
      BotUtils.sendOwnerMessage("Shutting down...", "Goodbye world.");
    }

    Globals.CLIENT.logout().block();
    System.exit(restart ? 1 : 0);
  }

  /**
   * Returns the usage string for the ShutdownCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().toString().equals(Globals.CONFIG.getOwner())) {
      return "__**The following commands must be used in a PM, else they are ignored.**__\n\n"
          + "`shutdown` - Shuts the bot down.\n\n"
          + "`restart` or `reboot` "
          + "- Updates and restarts the bot. Only works if using the provided script.";
    }

    return "";
  }
}
