package xyz.gupton.nickolas.beepsky;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ServiceLoader;

public class BotUtils {

  static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static DiscordClient CLIENT;
  public static GatewayDiscordClient GATEWAY;
  public static final String PREFIX = ";";
  public static final ServiceLoader<Command> commands = ServiceLoader.load(Command.class);
  static long startTime;

  /**
   * Sends a message with default color (dark gray).
   *
   * @param channel MessageChannel, channel to send the message to.
   * @param author User, author of the original commands we are replying to.
   * @param title String, title of the message to send.
   * @param description String, body of the message to send.
   */
  public static void sendMessage(MessageChannel channel, User author, String title,
      String description) {
    sendMessage(channel, author, title, description, Color.of(44, 47, 51));
  }

  /**
   * Sends a message.
   *
   * @param channel MessageChannel, channel to send the message to.
   * @param author User, author of the original commands we are replying to.
   * @param title String, title of the message to send.
   * @param description String, body of the message to send.
   * @param color Color, color of the message to send.
   */
  public static void sendMessage(MessageChannel channel, User author, String title,
      String description, Color color) {
    if (channel.getType() == Type.GUILD_TEXT) {
      if (!((GuildChannel)channel).getEffectivePermissions(BotUtils.GATEWAY.getSelfId())
          .block().contains(Permission.SEND_MESSAGES)) {
        return;
      }
    }

    channel.createMessage(messageSpec ->
      messageSpec.setEmbed(embedSpec -> embedSpec.setTitle(title).setDescription(description)
          .setFooter("Requested by: " + author.getUsername() + '#' + author.getDiscriminator()
              + " | Version: " + VERSION, null).setColor(color))
    ).block();

    //TODO: replace this with ScheduledExecutorService
    //if its not a private channel cleanup the messages after a few minutes
    //if (channel.getType() != Type.DM) {
    //  Timer timer = new Timer();

    //  timer.schedule(new TimerTask() {
    //    @Override
    //    public void run() {
    //      try {
    //        message.block().delete();
    //      } catch (NullPointerException e) {
    //        e.printStackTrace();
    //      }
    //    }
    //  }, TimeUnit.MINUTES.toMillis(5));
    //}
  }

  /**
   * Tests if the provided user is banned.
   *
   * @param userId String, ID of the Discord user to check.
   * @return boolean, true if they are banned
   */
  public static boolean isBanned(String userId) {
    try {
      // feels Java man
      BufferedReader banBuffer = new BufferedReader(new FileReader(new File("banned.txt")));
      String line;

      while ((line = banBuffer.readLine()) != null) {
        if (line.equals(userId)) {
          banBuffer.close();
          return true;
        }
      }

      banBuffer.close();
      return false;
    } catch (FileNotFoundException e) {
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
