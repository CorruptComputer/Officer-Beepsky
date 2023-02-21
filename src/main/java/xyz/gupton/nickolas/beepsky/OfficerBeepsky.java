package xyz.gupton.nickolas.beepsky;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.http.client.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gupton.nickolas.beepsky.owner.Owner;


class OfficerBeepsky {
  private final static Logger _logger = LoggerFactory.getLogger(OfficerBeepsky.class);

  /**
   * The main method for the bot, handles login, registering listeners, and alerting the owner.
   *
   * @param args Must be in the form [Discord token, Owner ID]
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      _logger.error("Usage: java -jar Officer-Beepsky-x.x.x.jar <Discord token> <Owner ID>");
      System.exit(0);
    }

    try {
      BotUtils.getInstance().GATEWAY =
          DiscordClient.create(args[0]).gateway()
              .setEnabledIntents(IntentSet.all())
              .login().block();
    } catch (ClientException e) {
      _logger.error("Invalid token, aborting...");
      System.exit(0);
    }

    // Register listeners via the EventSubscriber annotation which allows for organisation and
    // delegation of events
    if (BotUtils.getInstance().GATEWAY == null) {
      _logger.error("Gateway is null, aborting startup...");
      System.exit(0);
    }

    _logger.info("Registering event handlers");
    BotUtils.getInstance().GATEWAY.on(MessageCreateEvent.class)
            .subscribe(EventHandlers::messageCreateEventHandler);
    BotUtils.getInstance().GATEWAY.on(DisconnectEvent.class)
            .subscribe(EventHandlers::disconnectEventHandler);
    BotUtils.getInstance().GATEWAY.on(ReadyEvent.class)
            .subscribe(EventHandlers::readyEventHandler);

    _logger.info("Setting owner");
    Owner.OWNER_USER = Snowflake.of(Long.parseUnsignedLong(args[1]));

    BotUtils.getInstance().GATEWAY.onDisconnect().block();
  }
}
