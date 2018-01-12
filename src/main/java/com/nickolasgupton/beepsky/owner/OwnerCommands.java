package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class OwnerCommands implements Command {

  @Override
  public boolean shouldExecute(MessageReceivedEvent event) {
    return event.getChannel().isPrivate() && event.getAuthor().getLongID() == Owner.ID;
  }

  @Override
  public void execute(MessageReceivedEvent event) {

    switch (event.getMessage().getContent().split(" ")[0].toLowerCase()) {
      case "shutdown":
        shutdown(false);
        break;
      case "restart":
      case "reboot":
        shutdown(true);
        break;
      case "ban":
        if (event.getMessage().getContent().split(" ").length == 2) {
          UserBans.ban(event.getMessage().getContent().split(" ")[1]);
        }
        break;
      case "unban":
        if (event.getMessage().getContent().split(" ").length == 2) {
          UserBans.unban(event.getMessage().getContent().split(" ")[1]);
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void getCommands(MessageReceivedEvent event) {
  }

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
}

