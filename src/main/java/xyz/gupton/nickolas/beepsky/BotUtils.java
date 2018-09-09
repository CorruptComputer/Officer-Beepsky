package xyz.gupton.nickolas.beepsky;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static IDiscordClient CLIENT;
  public static final String PREFIX = ";";
  static long startTime;

  /**
   * Sends a message, and cleans it up after 5 minutes.
   * @param channel Text channel to send the message to.
   * @param author Author of the original commands we are replying to.
   * @param message Message to send.
   */
  public static void sendMessage(IChannel channel, IUser author, EmbedBuilder message) {
    message.withFooterText("Requested by: " + author.getName() + '#' + author.getDiscriminator()
        + " | Version: " + VERSION);
    IMessage messageSent = RequestBuffer.request(() -> channel.sendMessage(message.build())).get();

    // if its not a private channel cleanup the messages after a few minutes
    if (!channel.isPrivate()) {
      Timer timer = new Timer();

      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          messageSent.delete();
        }
      }, TimeUnit.MINUTES.toMillis(5));
    }
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
