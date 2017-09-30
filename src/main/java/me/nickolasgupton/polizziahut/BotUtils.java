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





    // Helper functions to make certain aspects of the bot easier to use.
//    public static void sendMessage(IChannel channel, String message){
//
//        // This might look weird but it'll be explained in another page.
//        RequestBuffer.request(() -> {
//            try{
//                channel.sendMessage(message);
//            } catch (DiscordException e){
//                System.err.println("Message could not be sent with error: ");
//                e.printStackTrace();
//            }
//        });
//    }
}
