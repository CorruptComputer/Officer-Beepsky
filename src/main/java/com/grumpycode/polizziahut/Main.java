package com.grumpycode.polizziahut;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.ReadyEvent;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException{

        String token;

        if (args.length == 0) {
            Scanner input = new Scanner(System.in);
            System.out.print("Token: ");
            token = input.nextLine();
        }else{
            token = args[0];
        }

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
