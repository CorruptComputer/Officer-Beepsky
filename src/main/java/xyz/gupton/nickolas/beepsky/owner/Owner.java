package xyz.gupton.nickolas.beepsky.owner;

import xyz.gupton.nickolas.beepsky.BotUtils;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class Owner {

  public static IUser user;

  public static void sendMessage(EmbedBuilder message) {
    BotUtils.sendMessage(user.getOrCreatePMChannel(), user, message);
  }
}
