package com.nickolasgupton.beepsky;

import com.nickolasgupton.beepsky.commands.GameCommands;
import com.nickolasgupton.beepsky.commands.GeneralCommands;
import com.nickolasgupton.beepsky.commands.MusicCommands;

import com.nickolasgupton.beepsky.commands.OwnerCommands;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler {

    // static maps of commands mapping from command string to the functional impl
    private static final Map<String, Command> generalCommands = GeneralCommands.getGeneralCommands();
    private static final Map<String, Command> ownerCommands = OwnerCommands.getOwnerCommands();
    private static final Map<String, Command> musicCommands = MusicCommands.getMusicCommands();
    private static final Map<String, Command> gameCommands = GameCommands.getGameCommands();

    @EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {

        if(BotUtils.isBanned(event.getAuthor().getStringID())) return;

        // Given a message "/test arg1 arg2", argArray will contain ["/test", "arg1", "arg"]
        String[] argArray = event.getMessage().getContent().split(" ");

        // First ensure at least the command and prefix is present, the arg length can be handled by your command func
        if (argArray.length == 0)
            return;

        // Check if the first arg (the command) starts with the prefix defined in the utils class
        if (argArray[0].startsWith(BotUtils.DEFAULT_PREFIX)){

            // Extract the "command" part of the first arg out by ditching the amount of characters present in the prefix
            String commandStr = argArray[0].substring(BotUtils.DEFAULT_PREFIX.length());

            // Load the rest of the args in the array into a List for safer access
            List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
            argsList.remove(0); // Remove the command

            // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
            if (generalCommands.containsKey(commandStr)) {
                generalCommands.get(commandStr).runCommand(event, argsList);
            }
        }else if (argArray[0].startsWith(BotUtils.MUSIC_PREFIX)){

            // Extract the "command" part of the first arg out by ditching the amount of characters present in the prefix
            String commandStr = argArray[0].substring(BotUtils.MUSIC_PREFIX.length());

            // Load the rest of the args in the array into a List for safer access
            List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
            argsList.remove(0); // Remove the command

            // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
            if (musicCommands.containsKey(commandStr)) {
                musicCommands.get(commandStr).runCommand(event, argsList);
            }
        }else if (argArray[0].startsWith(BotUtils.GAME_PREFIX)){
            // Extract the "command" part of the first arg out by ditching the amount of characters present in the prefix
            String commandStr = argArray[0].substring(BotUtils.GAME_PREFIX.length());

            // Load the rest of the args in the array into a List for safer access
            List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
            argsList.remove(0); // Remove the command

            // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
            if (gameCommands.containsKey(commandStr)) {
                gameCommands.get(commandStr).runCommand(event, argsList);
            }
        // else the only thing it could be an owner command, if it is not a private message,
        // or if it is not the owner just silently ignore it
        }else{
            if(event.getChannel().isPrivate() && event.getAuthor().getLongID() == BotUtils.OWNER_ID){
                String commandStr = argArray[0];

                // Load the rest of the args in the array into a List for safer access
                List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
                argsList.remove(0); // Remove the command

                // Instead of delegating the work to a switch, automatically do it via calling the mapping if it exists
                if (ownerCommands.containsKey(commandStr)) {
                    ownerCommands.get(commandStr).runCommand(event, argsList);
                }
            }
        }
    }
}
