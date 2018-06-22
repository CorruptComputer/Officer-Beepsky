package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class AnnouncementCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a command should be executed.
   *
   * @param message The message received.
   * @return True if the command should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      if (message.toString().split(" ", 2).length != 2) {
        return false;
      }
      return (message.toString().toLowerCase().startsWith("announcement"));
    }

    return false;
  }

  /**
   * Executes the command if it exists.
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
   * Returns the usage string for a command.
   *
   * @return String of the correct usage for the command.
   */
  @Override
  public String getCommand(IUser recipient) {
    if (recipient == Owner.user) {
      return "`announcement <message>` - Announces the message to all joined servers.";
    }

    return "";
  }
}
