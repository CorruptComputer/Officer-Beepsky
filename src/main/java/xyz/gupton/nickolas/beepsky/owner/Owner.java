package xyz.gupton.nickolas.beepsky.owner;

import discord4j.core.object.util.Snowflake;
import xyz.gupton.nickolas.beepsky.BotUtils;

public class Owner {

  public static Snowflake USER;

  /**
   * Sends a message to the Owner of the bot.
   *
   * @param title String, title of the message to send.
   * @param description String, description of the message to send.
   */
  public static void sendMessage(String title, String description) {
    try {
      BotUtils.CLIENT.getUserById(USER).block().getPrivateChannel().block()
          .createMessage(messageSpec ->
              messageSpec
                  .setEmbed(embedSpec -> embedSpec.setTitle(title).setDescription(description))
          ).block();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }
}
