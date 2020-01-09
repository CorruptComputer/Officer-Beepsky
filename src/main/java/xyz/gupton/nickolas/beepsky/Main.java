package xyz.gupton.nickolas.beepsky;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.rest.http.client.ClientException;
import xyz.gupton.nickolas.beepsky.configuration.Configuration;

class Main {

    /**
     * The main method for the bot, handles login, registering listeners, and alerting the owner.
     *
     * @param args None
     */
    public static void main(String[] args) {
        Globals.CONFIG = new Configuration();

        System.out.println("Token: " + Globals.CONFIG.getDiscordToken());
        System.out.println("Owner: " + Globals.CONFIG.getOwner());

        try {
            Globals.CLIENT = new DiscordClientBuilder(Globals.CONFIG.getDiscordToken()).build();
        } catch (ClientException e) {
            System.out.println("Invalid token, aborting...");
            System.exit(0);
        }

        // Register listeners via the EventSubscriber annotation which allows for organisation and
        // delegation of events
        Globals.CLIENT.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(CommandHandler::onMessageReceived);
        Globals.CLIENT.getEventDispatcher().on(DisconnectEvent.class)
                .subscribe(DisconnectHandler::onDisconnect);

        Globals.CLIENT.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    // the "Playing:" text
                    Globals.CLIENT.updatePresence(
                            Presence.online(Activity.playing(Globals.PREFIX + "help for commands"))).block();

                    BotUtils.sendOwnerMessage("Startup complete!", "Current version: " + Globals.VERSION);
                    Globals.startTime = System.currentTimeMillis();
                });

        // Only login after all events are registered otherwise some may be missed.
        Globals.CLIENT.login().block();
    }
}
