package xyz.gupton.nickolas.beepsky;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;

// Interface to determine if a commands should be executed, and then handles its execution
public interface Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message);

  /**
   * Executes the command.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  void execute(Guild guild, User author, MessageChannel channel, String message);

  /**
   * Returns the usage string for the command.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  String getCommand(User recipient);
}
