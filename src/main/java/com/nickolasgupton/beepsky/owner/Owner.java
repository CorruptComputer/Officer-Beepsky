package com.nickolasgupton.beepsky.owner;

import com.nickolasgupton.beepsky.BotUtils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class Owner {

  public static IUser user;

  public static void sendMessage(EmbedBuilder message) {
    BotUtils.sendMessage(user.getOrCreatePMChannel(), user, message);
  }

  public static String getOwnerName() {
    return user.getName();
  }
}
