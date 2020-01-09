package xyz.gupton.nickolas.beepsky;

import discord4j.core.DiscordClient;
import xyz.gupton.nickolas.beepsky.configuration.Configuration;

import java.util.ServiceLoader;

public class Globals {
    public static Configuration CONFIG;
    public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
    public static DiscordClient CLIENT;
    public static final String PREFIX = ";";
    public static final ServiceLoader<Command> commands = ServiceLoader.load(Command.class);
    public static long startTime;
}
