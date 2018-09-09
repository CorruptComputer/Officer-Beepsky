package xyz.gupton.nickolas.beepsky.owner.commands;

import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class LeaveCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate() && message.getAuthor() == Owner.user) {
      if (message.toString().split(" ").length != 2) {
        return false;
      }
      return (message.toString().toLowerCase().startsWith("leave"));
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
    BotUtils.CLIENT.getGuildByID(Long.parseLong(event.getMessage().toString()
        .split(" ", 2)[1])).leave();
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    if (recipient == Owner.user) {
      return "`leave <Server ID>` - Leaves that server.";
    }

    return "";
  }
}
