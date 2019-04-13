package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class AnnouncementCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null && author.getId().equals(Owner.USER)) {
      if (message.split(" ", 2).length != 2) {
        return false;
      }

      return message.toLowerCase().startsWith("announcement");
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
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    for (Guild g : BotUtils.CLIENT.getGuilds().toIterable()) {
      System.out.println(g.getName() + "\t\t" + g.getSystemChannel().block().getName());
      BotUtils.sendMessage(g.getSystemChannel().block(),
          BotUtils.CLIENT.getUserById(Owner.USER).block(), "New Announcement!",
          message.split(" ", 2)[1]);
    }
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
      return "`announcement <message>` - Announces the message to all joined servers.";
    }

    return "";
  }
}
