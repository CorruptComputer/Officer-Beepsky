package com.nickolasgupton.beepsky;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;

public class DisconnectHandler {
  /**
   * Sends a message.
   * @param event Provided by D4J.
   */
  @EventSubscriber
  public void onDisconnect(DisconnectedEvent event) {
    System.out.println("\n\nShutdown reason: " + event.getReason());
    System.exit(1);
  }
}
