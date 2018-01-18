package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import sx.blah.discord.util.EmbedBuilder;

public class Owner {
  public static long ID;

  public static void sendMessage(String message) {
    BotUtils.CLIENT.getUserByID(ID).getOrCreatePMChannel().sendMessage(message);
  }

  public static void sendMessage(EmbedBuilder message) {
    BotUtils.CLIENT.getUserByID(ID).getOrCreatePMChannel().sendMessage(message.build());
  }
}
