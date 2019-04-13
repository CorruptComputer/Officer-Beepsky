package xyz.gupton.nickolas.beepsky;

import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;
import java.util.concurrent.TimeUnit;

class DisconnectHandler {

  /**
   * Fired when disconnected from Discord for any reason.
   *
   * @param event Provided by Discord4j.
   */
  static void onDisconnect(DisconnectEvent event) {
    // When disconnected the 'now playing' text will get reset, so if we've been up for less than 24
    // hours there is no need to restart.
    if (System.currentTimeMillis() - BotUtils.startTime < TimeUnit.HOURS.toMillis(24)) {
      System.out.println("\n\nDisconnect reason: \n" + event.toString() + '\n');

      try {
        TimeUnit.SECONDS.wait(30);
      } catch (InterruptedException e) {
        System.out.println("Error waiting: \n");
        e.printStackTrace();
      }

      BotUtils.CLIENT
          .updatePresence(Presence.online(Activity.playing(BotUtils.PREFIX + "help for commands")))
          .block();
    } else { // else, lets just take the chance to update and restart.
      System.out.println("\n\nDisconnect reason: \n" + event.toString() + "\n\nRestarting...");
      System.exit(1);
    }
  }
}
