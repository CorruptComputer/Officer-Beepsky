package me.nickolasgupton.polizziahut;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {

    public static final String VERSION = BotUtils.class.getPackage().getImplementationVersion();
    public static String OWNER_ID;

    // Constants for use throughout the bot
    public static final String DEFAULT_PREFIX = ".";
    public static final String MUSIC_PREFIX = "!!";
    public static final String GAME_PREFIX = "$";

    // Handles the creation and getting of a IDiscordClient object for a token
    static IDiscordClient getBuiltDiscordClient(String token){

        // The ClientBuilder object is where you will attach your params for configuring the instance of your bot.
        // Such as withToken, setDaemon etc
        return new ClientBuilder()
                .withToken(token)
                .withRecommendedShardCount()
                .build();

    }

    // Helper functions to make certain aspects of the bot easier to use.
    public static void sendMessage(IChannel channel, String message){

        // This might look weird but it'll be explained in another page.
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
        });

    }
}
