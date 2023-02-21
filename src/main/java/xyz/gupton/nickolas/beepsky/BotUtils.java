package xyz.gupton.nickolas.beepsky;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * General utilities required by the bot.
 */
public final class BotUtils {
  public String VERSION;
  public GatewayDiscordClient GATEWAY;
  public String PREFIX;
  public ServiceLoader<Command> COMMANDS;
  public final long startTime;

  private static BotUtils INSTANCE;

  private BotUtils() {
    String version = BotUtils.class.getPackage().getImplementationVersion();
    VERSION = version != null ? version : "dev";

    PREFIX = ";";
    COMMANDS = ServiceLoader.load(Command.class);
    startTime = System.currentTimeMillis();
  }

  public static BotUtils getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new BotUtils();
    }

    return INSTANCE;
  }

  /**
   * Sends a message with default color (dark gray).
   *
   * @param channel     MessageChannel, channel to send the message to.
   * @param author      User, author of the original commands we are replying to.
   * @param title       String, title of the message to send.
   * @param description String, body of the message to send.
   */
  public void sendMessage(MessageChannel channel, User author, String title,
                                 String description) {
    sendMessage(channel, author, title, description, Color.of(44, 47, 51));
  }

  /**
   * Sends a message.
   *
   * @param channel     MessageChannel, channel to send the message to.
   * @param author      User, author of the original commands we are replying to.
   * @param title       String, title of the message to send.
   * @param description String, body of the message to send.
   * @param color       Color, color of the message to send.
   */
  public void sendMessage(MessageChannel channel, User author, String title,
                                 String description, Color color) {
    if (channel.getType() == Type.GUILD_TEXT) {
      PermissionSet ps = ((GuildChannel) channel)
              .getEffectivePermissions(GATEWAY.getSelfId()).block();
      if (ps != null && !ps.contains(Permission.SEND_MESSAGES)) {
        return;
      }
    }

    Message message = channel.createMessage(
            MessageCreateSpec.builder()
                    .addEmbed(
                            EmbedCreateSpec.builder()
                                    .title(title)
                                    .description(description)
                                    .footer("Requested by: " + author.getUsername() + '#' + author.getDiscriminator()
                                            + " | Version: " + VERSION, null)
                                    .color(color)
                                    .build()
                    ).build()
    ).block();

    //if it is not a private channel cleanup the messages after a few minutes
    if (channel.getType() != Type.DM) {
      Timer timer = new Timer();

      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            message.delete().block();
          } catch (NullPointerException e) {
            e.printStackTrace();
          }
        }
      }, TimeUnit.MINUTES.toMillis(5));
    }
  }

  /**
   * Tests if the provided user is banned.
   *
   * @param userId String, ID of the Discord user to check.
   * @return boolean, true if they are banned
   */
  public static boolean isBanned(String userId) {
    try (BufferedReader banBuffer =
                 Files.newBufferedReader(Path.of("banned.txt"), StandardCharsets.UTF_8)) {
      String line;

      while ((line = banBuffer.readLine()) != null) {
        if (line.equals(userId)) {
          banBuffer.close();
          return true;
        }
      }

      return false;
    } catch (NoSuchFileException e) {
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }
}
