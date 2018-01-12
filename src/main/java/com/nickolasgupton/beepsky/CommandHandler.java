package com.nickolasgupton.beepsky;

import com.nickolasgupton.beepsky.owner.UserBans;
import java.util.ServiceLoader;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CommandHandler {

  /**
   * Called when a messaged is received by the bot, whether it be private or in a server.
   *
   * @param event Provided by D4J
   */
  @EventSubscriber
  public void onMessageReceived(MessageReceivedEvent event) {
    if (!UserBans.isBanned(event.getAuthor().getStringID())) {
      ServiceLoader<Command> serviceLoader = ServiceLoader.load(Command.class);
      for (Command commands : serviceLoader) {
        if (commands.shouldExecute(event)) {
          commands.execute(event);
        }
      }
    }
  }
}
