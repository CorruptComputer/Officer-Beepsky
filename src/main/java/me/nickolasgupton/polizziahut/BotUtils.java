package me.nickolasgupton.polizziahut;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

// Mostly constants to be used throughout the bot
public class BotUtils {

    public static long OWNER_ID;
    public static IDiscordClient CLIENT;

    // Constants for use throughout the bot
    public static final String DEFAULT_PREFIX = ".";
    public static final String MUSIC_PREFIX = "!!";
    public static final String GAME_PREFIX = "$";
    public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
}
