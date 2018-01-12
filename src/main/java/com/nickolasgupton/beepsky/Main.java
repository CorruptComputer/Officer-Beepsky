package com.nickolasgupton.beepsky;

import com.nickolasgupton.beepsky.music.VoiceEvents;
import com.nickolasgupton.beepsky.owner.Owner;
import sx.blah.discord.api.ClientBuilder;

public class Main {

  /**
   * The main method for the bot, only handles login and registering listeners.
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
    Owner.ID = Long.parseUnsignedLong(args[1]);

    // Register a listener via the EventSubscriber annotation which allows for organisation and
    // delegation of events
    BotUtils.CLIENT.getDispatcher().registerListener(new CommandHandler());
    BotUtils.CLIENT.getDispatcher().registerListener(new VoiceEvents());
    BotUtils.CLIENT.getDispatcher().registerListener(new OnReady());

    // Only login after all events are registered otherwise some may be missed.
    BotUtils.CLIENT.login();
  }
}
