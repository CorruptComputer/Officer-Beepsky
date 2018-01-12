package com.nickolasgupton.beepsky;

import sx.blah.discord.api.IDiscordClient;

// Constants to be used throughout the bot
public class BotUtils {

  public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
  public static IDiscordClient CLIENT;
}
