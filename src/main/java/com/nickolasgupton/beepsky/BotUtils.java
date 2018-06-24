package com.nickolasgupton.beepsky;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static IDiscordClient CLIENT;
  public static final String PREFIX = ";";
  static long startTime;

  /**
   * Sends a message.
   * @param channel Text channel to send the message to.
   * @param author Author of the original command we are replying to.
   * @param message Message to send.
   */
  public static void sendMessage(IChannel channel, IUser author, EmbedBuilder message) {
    message.withFooterText("Requested by: " + author.getName() + '#' + author.getDiscriminator()
        + " | Version: " + VERSION);
    RequestBuffer.request(() -> channel.sendMessage(message.build()));
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
      return false;
    }
  }
}
