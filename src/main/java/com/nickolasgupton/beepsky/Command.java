package com.nickolasgupton.beepsky;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

// Interface to determine if a command should be executed, and then handles its execution
public interface Command {

  boolean shouldExecute(MessageReceivedEvent event);

  void execute(MessageReceivedEvent event);

  void getCommands(MessageReceivedEvent event);
}
