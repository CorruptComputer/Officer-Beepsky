package xyz.gupton.nickolas.beepsky.owner.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class ShutdownCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate() && message.getAuthor() == Owner.user) {
      return (message.toString().toLowerCase().equals("shutdown")
          || message.toString().toLowerCase().equals("restart")
          || message.toString().toLowerCase().equals("reboot"));
    }

    return false;
  }

  /**
   * Executes the commands if it exists.
   *
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);

    boolean restart = !event.getMessage().toString().toLowerCase().equals("shutdown");

    if (restart) {
      builder.withTitle("Restarting...");
      builder.withDescription("This may take up to a few minutes if an update is available.");
    } else {
      builder.withTitle("Shutting down...");
      builder.withDescription("Goodbye world.");
    }

    builder.withFooterText("Current version: " + BotUtils.VERSION);
    Owner.sendMessage(builder);

    BotUtils.CLIENT.logout();
    System.exit(restart ? 1 : 0);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    if (recipient == Owner.user) {
      return "__**The following commands must be used in a PM, else they are ignored.**__\n\n"
          + "`shutdown` - Shuts the bot down.\n\n"
          + "`restart` or `reboot` "
          + "- Updates and restarts the bot. Only works if using the provided script.";
    }

    return "";
  }
}
