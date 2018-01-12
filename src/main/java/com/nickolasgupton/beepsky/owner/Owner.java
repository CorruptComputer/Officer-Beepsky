package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class Owner {
  public static long ID;

  public static void sendMessage(String message) {
    RequestBuffer.request(
        () -> BotUtils.CLIENT.getUserByID(ID).getOrCreatePMChannel().sendMessage(message));
  }

  public static void sendMessage(EmbedBuilder message) {
    RequestBuffer.request(
        () -> BotUtils.CLIENT.getUserByID(ID).getOrCreatePMChannel().sendMessage(message.build()));
  }
}
