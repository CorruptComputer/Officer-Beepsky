package com.nickolasgupton.beepsky;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static IDiscordClient CLIENT;

  public static void sendMessage(IChannel channel, EmbedObject message) {
    RequestBuffer.request(() -> channel.sendMessage(message));
  }
}
