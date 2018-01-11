package com.nickolasgupton.beepsky;

import com.nickolasgupton.beepsky.music.Events;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;


public class Main {

  /**
   * The main method for the bot.
   *
   * @param args Must be in the form [Discord token, Owner ID]
   * @throws InterruptedException The bot can be interrupted, if so it will usually terminate
   *        itself
   */
  public static void main(String[] args) throws InterruptedException {

    if (args.length < 2) {
      System.out.println("Usage: java -jar PolizziaHut-x.x.x.jar <Discord token> <Owner ID>");
      System.exit(1);
    }

    // setup client and owner ID
    BotUtils.CLIENT = new ClientBuilder().withToken(args[0]).withRecommendedShardCount().build();
    BotUtils.OWNER_ID = Long.parseUnsignedLong(args[1]);

    // Register a listener via the EventSubscriber annotation which allows for organisation and
    // delegation of events
    BotUtils.CLIENT.getDispatcher().registerListener(new CommandHandler());
    BotUtils.CLIENT.getDispatcher().registerListener(new Events());

    // Only login after all events are registered otherwise some may be missed.
    BotUtils.CLIENT.login();

    BotUtils.CLIENT.getDispatcher().waitFor(ReadyEvent.class);

    // the "Now Playing:" text
    BotUtils.CLIENT.changePlayingText(".help for commands");

    // send message of successful startup
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("Startup complete!");
    builder.withDescription("Current version: " + BotUtils.VERSION);
    RequestBuffer.request(
        () -> BotUtils.CLIENT.getUserByID(BotUtils.OWNER_ID).getOrCreatePMChannel()
            .sendMessage(builder.build()));
  }
}
