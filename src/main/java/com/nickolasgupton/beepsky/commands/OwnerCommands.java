package com.nickolasgupton.beepsky.commands;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class OwnerCommands {

  /**
   * Owner Only Commands are defined here.
   *
   * @return returns the map of Owner Commands
   */
  public static Map<String, Command> getOwnerCommands() {
    Map<String, Command> ownerCommands = new HashMap<>();

    ownerCommands.put("shutdown", (event, args) -> {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(100, 255, 100);
      builder.withTitle("Shutting down...");
      builder.withDescription("Goodbye world.");
      builder.withFooterText("Current version: " + BotUtils.VERSION);
      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

      System.exit(0);
    });

    ownerCommands.put("restart", (event, args) -> {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(100, 255, 100);
      builder.withTitle("Restarting...");
      builder.withDescription("This may take up to a few minutes if an update is available.");
      builder.withFooterText("Current version: " + BotUtils.VERSION);
      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

      System.exit(1);
    });

    ownerCommands.put("ban", (event, args) -> {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(100, 255, 100);

      if (BotUtils.isBanned(args.get(0))) {
        builder.withColor(255, 0, 0);
        builder.withTitle("Error Banning");
        builder.withDescription(
            BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(args.get(0))).getName()
                + " is already banned.");
        RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
        return;
      }

      try {
        Writer output;
        output = new BufferedWriter(new FileWriter("banned.txt", true));

        output.append(args.get(0));
        output.append('\n');

        output.close();

        builder.withTitle("Ban Successful");
        builder.withDescription(
            BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(args.get(0))).getName()
                + " is now banned.");
      } catch (Exception e) {
        builder.withColor(255, 0, 0);
        e.printStackTrace();
        builder.withTitle("Error Banning");
        builder.withDescription("Try again later.");
      }

      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
    });

    ownerCommands.put("unban", (event, args) -> {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(100, 255, 100);

      if (!BotUtils.isBanned(args.get(0))) {
        builder.withColor(255, 0, 0);
        builder.withTitle("Error Unbanning");
        builder.withDescription(
            BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(args.get(0))).getName()
                + " is not banned.");
        RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
        return;
      }

      try {
        File file = new File("banned.txt");
        List<String> out = Files.lines(file.toPath())
            .filter(line -> !line.contains(args.get(0)))
            .collect(Collectors.toList());
        Files.write(file.toPath(), out, StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);

        builder.withTitle("Unban Successful");
        builder.withDescription(
            BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(args.get(0))).getName()
                + " has been unbanned.");
      } catch (Exception e) {
        builder.withColor(255, 0, 0);
        e.printStackTrace();
        builder.withTitle("Error Unbanning");
        builder.withDescription("Try again later.");
      }

      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
    });

    return ownerCommands;
  }
}

