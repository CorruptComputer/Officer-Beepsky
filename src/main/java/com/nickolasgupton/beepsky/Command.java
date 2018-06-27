package com.nickolasgupton.beepsky;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

// Interface to determine if a commands should be executed, and then handles its execution
public interface Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  boolean shouldExecute(IMessage message);

  /**
   * Executes the commands if it exists.
   * @param event Provided by D4J.
   */
  void execute(MessageReceivedEvent event);

  /**
   * Returns the usage string for a commands.
   * @param recipient The user the commands is going to, used for permissions checking.
   * @return String of the correct usage for the commands.
   */
  String getCommand(IUser recipient);
}
