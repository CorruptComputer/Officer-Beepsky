package xyz.gupton.nickolas.beepsky;

import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.gupton.nickolas.beepsky.owner.Owner;

/**
 * Class handles the events fired by Discord4j.
 */
public class EventHandlers {
  private static Logger _logger = LoggerFactory.getLogger(EventHandlers.class);

  /**
   * Fired when disconnected from Discord for any reason.
   *
   * @param event Provided by Discord4j.
   */
  static void disconnectEventHandler(DisconnectEvent event) {
    // When disconnected the 'now playing' text will get reset, and if we've been up for less than
    // 24 hours there is no need to restart.
    if (System.currentTimeMillis() - BotUtils.startTime < TimeUnit.HOURS.toMillis(24)) {
      _logger.warn("\n\nDisconnect reason: \n" + event.toString() + '\n');

      try {
        TimeUnit.SECONDS.wait(30);
      } catch (InterruptedException e) {
        _logger.warn("Error waiting: \n");
        e.printStackTrace();
      }

      BotUtils.GATEWAY
              .updatePresence(
                      ClientPresence.online(
                              ClientActivity.listening(BotUtils.PREFIX + "help for commands")
                      )
              ).block();
    } else { // else, lets just take the chance to update and restart.
      _logger.warn("\n\nDisconnect reason: \n" + event.toString() + "\n\nRestarting...");
      System.exit(1);
    }
  }

  static void readyEventHandler(ReadyEvent event) {
    // the "Playing:" text
    BotUtils.GATEWAY.updatePresence(
            ClientPresence.online(
                    ClientActivity.listening(BotUtils.PREFIX + "help for commands")
            )
    ).block();

    _logger.warn("Ready");

    Owner.sendMessage("Startup complete!", "Current version: " + BotUtils.VERSION);

    BotUtils.startTime = System.currentTimeMillis();
  }

  static void messageCreateEventHandler(MessageCreateEvent event) {
    Guild guild = event.getGuild().blockOptional().orElse(null);
    User author = event.getMessage().getAuthor().orElse(null);
    MessageChannel channel = event.getMessage().getChannel().block();
    String message = event.getMessage().getContent();

    if (author == null
            || author.isBot()
            || BotUtils.isBanned(author.getId().asString())) {
      return;
    }

    // Search all available commands types
    for (Command command : BotUtils.commands) {

      // Check if the provided message should be executed
      if (command.shouldExecute(guild, author, channel, message)) {

        command.execute(guild, author, channel, message);

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            event.getMessage().delete().block();
          }
        }, TimeUnit.SECONDS.toMillis(10));
      }
    }
  }
}
