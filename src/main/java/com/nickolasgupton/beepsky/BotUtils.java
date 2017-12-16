package com.nickolasgupton.beepsky;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.*;

// Mostly constants to be used throughout the bot
public class BotUtils {

    public static long OWNER_ID;
    public static IDiscordClient CLIENT;

    // Constants for use throughout the bot
    public static final String DEFAULT_PREFIX = ".";
    public static final String MUSIC_PREFIX = "!!";
    public static final String GAME_PREFIX = "$";
    public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();

    public static boolean isBanned(String id) {
        try {
            //feels Java man
            BufferedReader banBuffer = new BufferedReader(new FileReader(new File("banned.txt")));
            String line;

            while ((line = banBuffer.readLine()) != null) {
                if (line.equals(id)) return true;
            }

            return false;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            builder.withTitle("Error in isBanned(), IOException:");
            builder.withDescription(exceptionAsString);

            RequestBuffer.request(() -> BotUtils.CLIENT.getUserByID(BotUtils.OWNER_ID).getOrCreatePMChannel().sendMessage(builder.build()));
            return false;
        }
    }
}
