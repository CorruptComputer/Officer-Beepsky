package com.nickolasgupton.beepsky;

import com.nickolasgupton.beepsky.owner.Owner;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.EmbedBuilder;

class OnReady {

  /**
   * Called by D4J when the bot is done with startup and is ready to use.
   * @param event Provided by D4J
   */
  @EventSubscriber
  public void onReady(ReadyEvent event) {
    // the "Now Playing:" text
    BotUtils.CLIENT.changePlayingText(".help for commands");

    // send message of successful startup to bot owner
    EmbedBuilder startupMessage = new EmbedBuilder();
    startupMessage.withColor(100, 255, 100);
    startupMessage.withTitle("Startup complete!");
    startupMessage.withDescription("Current version: " + BotUtils.VERSION);
    Owner.sendMessage(startupMessage);
  }
}
