package xyz.gupton.nickolas.beepsky.owner.commands;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class AnnouncementCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate() && message.getAuthor() == Owner.user) {
      if (message.toString().split(" ", 2).length != 2) {
        return false;
      }
      return (message.toString().toLowerCase().startsWith("announcement"));
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
    builder.withColor(255, 255, 255);
    builder.withTitle("New Announcement!");
    builder.withDescription(event.getMessage().toString().split(" ", 2)[1]);

    for (IGuild guild : BotUtils.CLIENT.getGuilds()) {
      System.out.println(guild.getName() + "       " + guild.getSystemChannel().getName());
      BotUtils.sendMessage(guild.getSystemChannel(), Owner.user, builder);
    }
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    if (recipient == Owner.user) {
      return "`announcement <message>` - Announces the message to all joined servers.";
    }

    return "";
  }
}
