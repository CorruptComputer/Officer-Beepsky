package xyz.gupton.nickolas.beepsky;

import java.util.concurrent.TimeUnit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

public class DisconnectHandler {
  /**
   * Fired when disconnected from Discord for any reason.
   * @param event Provided by D4J.
   */
  @EventSubscriber
  public void onDisconnect(DisconnectedEvent event) {
    // When disconnected the 'now playing' text will get reset, so if we've been up for less than 24
    // hours there is no need to restart.
    if (System.currentTimeMillis() - BotUtils.startTime < TimeUnit.HOURS.toMillis(24)) {
      System.out.println("\n\nDisconnect reason: \n" + event.getReason() + '\n');

      try {
        TimeUnit.SECONDS.wait(30);
      } catch (InterruptedException e) {
        System.out.println("Error waiting: \n");
        e.printStackTrace();
      }

      BotUtils.CLIENT.changePresence(StatusType.ONLINE, ActivityType.PLAYING,
          BotUtils.PREFIX + "help for commands");
    } else { // else, lets just take the chance to update and restart.
      System.out.println("\n\nDisconnect reason: \n" + event.getReason() + "\n\nRestarting...");
      System.exit(1);
    }
  }
}
