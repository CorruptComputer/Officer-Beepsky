package com.nickolasgupton.beepsky.general;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.util.ServiceLoader;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class GeneralCommands implements Command {

  private static String PREFIX = ".";

  @Override
  public boolean shouldExecute(MessageReceivedEvent event) {
    return event.getMessage().getContent().startsWith(PREFIX);
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    switch (event.getMessage().getContent().split(" ")[0].substring(PREFIX.length())
        .toLowerCase()) {
      case "help":
        help(event);
        break;
      default:
        break;
    }
  }

  @Override
  public void getCommands(MessageReceivedEvent event) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("General Commands:");
    builder.withDescription("`" + PREFIX
        + "help` - You should already know this one.\n\n"
        + "Officer-Beepsky is an open source Discord bot, you can view the source here on [GitHub](https://github.com/CorruptComputer/Officer-Beepsky).");

    builder.withFooterText("v" + BotUtils.VERSION);
    RequestBuffer
        .request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(builder.build()));
    event.getMessage().delete();
  }

  private static void help(MessageReceivedEvent event) {
    ServiceLoader<Command> serviceLoader = ServiceLoader.load(Command.class);
    for (Command commands : serviceLoader) {
      commands.getCommands(event);
    }
  }
}

