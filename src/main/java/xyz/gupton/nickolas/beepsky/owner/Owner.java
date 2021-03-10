package xyz.gupton.nickolas.beepsky.owner;

import discord4j.common.util.Snowflake;
import xyz.gupton.nickolas.beepsky.BotUtils;

public class Owner {

  public static Snowflake OWNER_USER;

  /**
   * Sends a message to the Owner of the bot.
   *
   * @param title String, title of the message to send.
   * @param description String, description of the message to send.
   */
  public static void sendMessage(String title, String description) {
    try {
      BotUtils.GATEWAY.getUserById(OWNER_USER).block().getPrivateChannel().block()
          .createMessage(messageSpec ->
              messageSpec
                  .setEmbed(embedSpec -> embedSpec.setTitle(title).setDescription(description))
          ).block();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }
}
