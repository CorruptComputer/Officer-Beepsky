package xyz.gupton.nickolas.beepsky.owner;

import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import xyz.gupton.nickolas.beepsky.BotUtils;

public class Owner {

  public static IUser user;

  public static void sendMessage(EmbedBuilder message) {
    BotUtils.sendMessage(user.getOrCreatePMChannel(), user, message);
  }
}
