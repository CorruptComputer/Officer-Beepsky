package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import sx.blah.discord.util.EmbedBuilder;

public class UserBans {

  /**
   * Bans the specified user from using the bot.
   * @param userId String ID of the Discord user to ban.
   */
  static void ban(String userId) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);

    if (userId.equals(String.valueOf(Owner.ID))) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error Banning");
      builder.withDescription("You cannot ban yourself!");
      Owner.sendMessage(builder);
      return;
    }

    if (isBanned(userId)) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error Banning");
      builder.withDescription(
          BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(userId)).getName()
              + " is already banned.");
      Owner.sendMessage(builder);
      return;
    }

    try {
      Writer output;
      output = new BufferedWriter(new FileWriter("banned.txt", true));

      output.append(userId);
      output.append('\n');

      output.close();

      builder.withTitle("Ban Successful");
      builder.withDescription(
          BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(userId)).getName()
              + " is now banned.");
    } catch (Exception e) {
      builder.withColor(255, 0, 0);
      e.printStackTrace();
      builder.withTitle("Error Banning");
      builder.withDescription("Error: " + e.getMessage());
    }

    Owner.sendMessage(builder);
  }

  /**
   * Unbans the specified user from using the bot.
   * @param userId String ID of the Discord user to unban.
   */
  static void unban(String userId) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);

    if (!isBanned(userId)) {
      builder.withColor(255, 0, 0);
      builder.withTitle("Error Unbanning");
      builder.withDescription(
          BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(userId)).getName()
              + " is not banned.");
      Owner.sendMessage(builder);
      return;
    }

    try {
      File file = new File("banned.txt");
      List<String> out = Files.lines(file.toPath())
          .filter(line -> !line.contains(userId))
          .collect(Collectors.toList());
      Files.write(file.toPath(), out, StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING);

      builder.withTitle("Unban Successful");
      builder.withDescription(
          BotUtils.CLIENT.getUserByID(Long.parseUnsignedLong(userId)).getName()
              + " has been unbanned.");
    } catch (Exception e) {
      builder.withColor(255, 0, 0);
      e.printStackTrace();
      builder.withTitle("Error Unbanning");
      builder.withDescription("Error: " + e.getMessage());
    }

    Owner.sendMessage(builder);
  }


  /**
   * Tests if the provided user is banned.
   *
   * @param userId string ID of the Discord user
   * @return boolean, true if they are banned
   */
  public static boolean isBanned(String userId) {
    try {
      // feels Java man
      BufferedReader banBuffer = new BufferedReader(new FileReader(new File("banned.txt")));
      String line;

      while ((line = banBuffer.readLine()) != null) {
        if (line.equals(userId)) {
          return true;
        }
      }

      return false;
    } catch (FileNotFoundException e) {
      return false;
    } catch (IOException e) {
      e.printStackTrace();

      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      String exceptionAsString = sw.toString();

      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(255, 0, 0);
      builder.withTitle("Error in isBanned(), IOException:");
      builder.withDescription(exceptionAsString);

      Owner.sendMessage(builder);
      return false;
    }
  }
}
