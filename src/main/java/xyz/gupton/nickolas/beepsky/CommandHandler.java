package xyz.gupton.nickolas.beepsky;

import java.util.ServiceLoader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

class CommandHandler {

  /**
   * Called when a messaged is received by the bot, whether it be private or in a server.
   *
   * @param event Provided by D4J
   */
  @EventSubscriber
  public void onMessageReceived(MessageReceivedEvent event) {
    // Only continue if the author is not banned
    if (!BotUtils.isBanned(event.getAuthor().getStringID())) {

      // Search all available commands types
      for (Command commands : ServiceLoader.load(Command.class)) {

        // Check if the provided message should be executed
        if (commands.shouldExecute(event.getMessage())) {
          commands.execute(event);

          Timer timer = new Timer();

          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              event.getMessage().delete();
            }
          }, TimeUnit.SECONDS.toMillis(10));
        }
      }

    }
  }
}
