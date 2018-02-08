package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class OwnerCommands implements Command {

  @Override
  public boolean shouldExecute(IMessage message) {
    return message.getChannel().isPrivate() && message.getAuthor().getLongID() == Owner.ID;
  }

  @Override
  public void execute(MessageReceivedEvent event) {

    String[] command = event.getMessage().getContent().split(" ", 2);
    switch (command[0].toLowerCase()) {
      case "shutdown":
        shutdown(false);
        break;
      case "restart":
      case "reboot":
        shutdown(true);
        break;
      case "ban":
        if (command.length == 2) {
          UserBans.ban(command[1]);
        }
        break;
      case "unban":
        if (command.length == 2) {
          UserBans.unban(command[1]);
        }
        break;
      case "leave":
        if (command.length == 2) {
          leave(command[1]);
        }
        break;
      case "announcement":
        if (command.length == 2) {
          sendAnnouncement(command[1]);
        }
        break;
      default:
        break;
    }
  }

  /**
   * Sends the available owner commands to the recipient.
   *
   * @param recipient Who the help message(s) should be sent to.
   */
  @Override
  public void getCommands(IPrivateChannel recipient) {
    if (recipient.getRecipient().getStringID().matches(Long.toString(Owner.ID))) {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(100, 255, 100);
      builder.withTitle("Owner Commands:");
      builder.withDescription(
          "These must be used in a PM to the bot, else they are ignored.\n\n"
              + "`shutdown` - Shuts the bot down.\n"
              + "`restart` "
              + "or `reboot` - Updates and restarts the bot. Only works if using the provided"
              + " script.\n"

              + "`ban <Discord ID>` "
              + "and `unban <Discord ID>` - Bans or unbans the user with that Discord ID.\n"

              + "`leave <Server ID>` - Leaves that server.\n"

              + "`announcement <message>` - Announces to all joined servers.\n");
      builder.withFooterText("v" + BotUtils.VERSION);
      RequestBuffer.request(() -> recipient.sendMessage(builder.build()));
    }
  }

  /**
   * Leaves the guild specified.
   *
   * @param serverId String ID of the server to leave.
   */
  private void leave(String serverId) {
    BotUtils.CLIENT.getGuildByID(Long.parseLong(serverId)).leave();
  }

  /**
   * Shuts down or restarts the bot.
   *
   * @param restart True if you want to restart using the provided script.
   */
  private void shutdown(Boolean restart) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);

    if (restart) {
      builder.withTitle("Restarting...");
      builder.withDescription("This may take up to a few minutes if an update is available.");
    } else {
      builder.withTitle("Shutting down...");
      builder.withDescription("Goodbye world.");
    }

    builder.withFooterText("Current version: " + BotUtils.VERSION);
    Owner.sendMessage(builder);

    System.exit(restart ? 1 : 0);
  }

  /**
   * Announces to all joined servers.
   * @param message Message to be announced.
   */
  private void sendAnnouncement(String message) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(255, 255, 255);
    builder.withTitle("New Announcement!");
    builder.withDescription(message);
    builder.withFooterText("From: " + Owner.getOwnerName());

    for (IGuild guild : BotUtils.CLIENT.getGuilds()) {
      System.out.println(guild.getName() + "       " + guild.getSystemChannel().getName());
      BotUtils.sendMessage(guild.getSystemChannel(), builder.build());
    }
  }
}

