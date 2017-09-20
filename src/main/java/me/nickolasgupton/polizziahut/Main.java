package me.nickolasgupton.polizziahut;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class Main {
    public static void main(String[] args) throws InterruptedException{

        if(args.length < 2){
            System.out.println("Usage: java -jar PolizziaHut-x.x.x.jar <Discord token> <Owner ID>");
            System.exit(1);
        }

        String token = args[0];

        BotUtils.OWNER_ID = args[1];

        IDiscordClient client = BotUtils.getBuiltDiscordClient(token);

        // Register a listener via the EventSubscriber annotation which allows for organisation and delegation of events
        client.getDispatcher().registerListener(new CommandHandler());

        // Only login after all events are registered otherwise some may be missed.
        client.login();

        client.getDispatcher().waitFor(ReadyEvent.class);

        // the "Now Playing:" text
        client.changePlayingText(".help for commands");
    }
}
