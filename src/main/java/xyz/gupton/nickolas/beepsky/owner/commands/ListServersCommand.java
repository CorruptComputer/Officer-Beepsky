package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

/**
 *  Command to list the servers the bot is joined to.
 */
public class ListServersCommand implements Command {

  /**
   * Checks the command and if it was sent in a PM to the bot.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null && author.getId().equals(Owner.OWNER_USER)) {
      return message.equalsIgnoreCase("listservers");
    }

    return false;
  }

  /**
   * Lists all servers the bot is a member of.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    StringBuilder sb = new StringBuilder();
    for (Guild g : BotUtils.getInstance().GATEWAY.getGuilds().toIterable()) {
      sb.append(g.getName());
      sb.append(" | ");
      sb.append(g.getId().asString());
      sb.append("\n");
    }
    Owner.sendMessage("Servers currently available", sb.toString());
  }

  /**
   * Returns the usage string for the ListServersCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.OWNER_USER)) {
      return "`listservers` - Lists all of the servers that the bot is joined to.";
    }

    return "";
  }
}