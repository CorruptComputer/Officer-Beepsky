package com.nickolasgupton.beepsky;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;

// Interface to determine if a command should be executed, and then handles its execution
public interface Command {

  /**
   * Checks things such as prefix and permissions to determine if a command should be executed.
   * @param event Provided by D4J.
   * @return True if the command should be executed.
   */
  boolean shouldExecute(MessageReceivedEvent event);

  /**
   * Executes the command if it exists.
   * @param event Provided by D4J.
   */
  void execute(MessageReceivedEvent event);

  /**
   * Sends the available [] commands to the recipient.
   * @param recipient Who the help message(s) should be sent to.
   */
  void getCommands(IPrivateChannel recipient);
}
