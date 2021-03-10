package xyz.gupton.nickolas.beepsky;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import discord4j.rest.http.client.ClientException;
import xyz.gupton.nickolas.beepsky.owner.Owner;

class OfficerBeepsky {

  /**
   * The main method for the bot, handles login, registering listeners, and alerting the owner.
   *
   * @param args Must be in the form [Discord token, Owner ID]
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: java -jar Officer-Beepsky-x.x.x.jar <Discord token> <Owner ID>");
      System.exit(0);
    }

    try {
      BotUtils.CLIENT = DiscordClient.create(args[0]);
      BotUtils.GATEWAY = BotUtils.CLIENT.login().block();
    } catch (ClientException e) {
      System.out.println("Invalid token, aborting...");
      System.exit(0);
    }

    // Register listeners via the EventSubscriber annotation which allows for organisation and
    // delegation of events
    if (BotUtils.GATEWAY == null) {
      System.out.println("Gateway is null, aborting startup...");
      System.exit(0);
    }

    BotUtils.GATEWAY.on(MessageCreateEvent.class)
        .subscribe(CommandHandler::onMessageReceived);
    BotUtils.GATEWAY.on(DisconnectEvent.class)
        .subscribe(DisconnectHandler::onDisconnect);

    BotUtils.GATEWAY.on(ReadyEvent.class)
        .subscribe(event -> {
          // the "Playing:" text
          BotUtils.GATEWAY.updatePresence(
              Presence.online(Activity.playing(BotUtils.PREFIX + "help for commands"))).block();
          Owner.OWNER_USER = Snowflake.of(Long.parseUnsignedLong(args[1]));

          Owner.sendMessage("Startup complete!", "Current version: " + BotUtils.VERSION);

          BotUtils.startTime = System.currentTimeMillis();
        });

    BotUtils.GATEWAY.onDisconnect().block();
  }
}
