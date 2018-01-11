package com.nickolasgupton.beepsky;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;


// Mostly constants to be used throughout the bot
public class BotUtils {

  // Constants for use throughout the bot
  public static final String DEFAULT_PREFIX = ".";
  public static final String MUSIC_PREFIX = "!!";
  public static final String GAME_PREFIX = "$";
  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static long OWNER_ID;
  public static IDiscordClient CLIENT;

  /**
   * Tests if the provided user is banned.
   *
   * @param id string ID of the Discord user
   * @return boolean, true if they are banned
   */

  public static boolean isBanned(String id) {
    try {
      // feels Java man
      BufferedReader banBuffer = new BufferedReader(new FileReader(new File("banned.txt")));
      String line;

      while ((line = banBuffer.readLine()) != null) {
        if (line.equals(id)) {
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

      RequestBuffer.request(
          () -> BotUtils.CLIENT.getUserByID(BotUtils.OWNER_ID).getOrCreatePMChannel()
              .sendMessage(builder.build()));
      return false;
    }
  }
}
