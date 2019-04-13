package xyz.gupton.nickolas.beepsky;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.util.Snowflake;
import discord4j.rest.http.client.ClientException;
import reactor.core.publisher.Mono;
import xyz.gupton.nickolas.beepsky.owner.Owner;

class Main {

  /**
   * The main method for the bot, handles login, registering listeners, and alerting the owner.
   *
   * @param args Must be in the form [Discord token, Owner ID]
   */
  public static void main(String[] args) {

    if (args.length != 2) {
      System.out.println("Usage: java -jar Officer-Beepsky-x.x.x.jar <Discord token> <Owner ID>");
      System.exit(1);
    }

    try {
      BotUtils.CLIENT = new DiscordClientBuilder(args[0]).build();
    } catch (ClientException e) {
      System.out.println("Invalid token, aborting...");
      System.exit(0);
    }

    // Register listeners via the EventSubscriber annotation which allows for organisation and
    // delegation of events
    BotUtils.CLIENT.getEventDispatcher().on(MessageCreateEvent.class)
        .subscribe(CommandHandler::onMessageReceived);
    BotUtils.CLIENT.getEventDispatcher().on(DisconnectEvent.class)
        .subscribe(DisconnectHandler::onDisconnect);

    BotUtils.CLIENT.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
          // the "Playing:" text
          BotUtils.CLIENT.updatePresence(
              Presence.online(Activity.playing(BotUtils.PREFIX + "help for commands"))).block();
          Owner.USER = Snowflake.of(Long.parseUnsignedLong(args[1]));

          Owner.sendMessage("Startup complete!", "Current version: " + BotUtils.VERSION);

          BotUtils.startTime = System.currentTimeMillis();
        });

    // Only login after all events are registered otherwise some may be missed.
    BotUtils.CLIENT.login().block();
  }
}
