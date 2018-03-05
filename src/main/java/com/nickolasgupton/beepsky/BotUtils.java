package com.nickolasgupton.beepsky;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static IDiscordClient CLIENT;

  /**
   * Sends a message.
   * @param channel Text channel to send the message to.
   * @param author Author of the original command we are replying to.
   * @param message Message to send.
   */
  public static void sendMessage(IChannel channel, IUser author, EmbedBuilder message) {
    message.withFooterText('v' + VERSION + " "
        + author.getName() + '#' + author.getDiscriminator());
    RequestBuffer.request(() -> channel.sendMessage(message.build()));
  }
}
