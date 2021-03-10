package xyz.gupton.nickolas.beepsky.owner;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
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
    User owner = BotUtils.GATEWAY.getUserById(OWNER_USER).block();
    if (owner == null) {
      return;
    }

    PrivateChannel ownerPrivateChannel = owner.getPrivateChannel().block();
    if (ownerPrivateChannel == null) {
      return;
    }

    ownerPrivateChannel.createMessage(messageSpec ->
        messageSpec.setEmbed(embedSpec -> embedSpec.setTitle(title).setDescription(description))
    ).block();
  }
}
