package com.grumpycode.polizziahut.commands;

import com.grumpycode.polizziahut.BotUtils;
import com.grumpycode.polizziahut.Command;
import com.grumpycode.polizziahut.MusicHelper;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.AudioPlayer;

import java.util.HashMap;
import java.util.Map;

public class MusicCommands {
    public static Map<String, Command> getMusicCommands() {
        Map<String, Command> musicCommands = new HashMap<>();

        // Plays the first song found containing the first arg
        musicCommands.put("queue", (event, args) -> {

            IVoiceChannel userVoiceChannel = event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel();

            if (userVoiceChannel == null) {
                BotUtils.sendMessage(event.getChannel(), "Not in a voice channel, join one and then try again.");
                return;
            }

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
            // if the bot is already in another channel don't move
            if(botVoiceChannel == null){
                // clear the queue before joining
                AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());

                audioP.clear();

                userVoiceChannel.join();
            }else if(botVoiceChannel != userVoiceChannel){
                BotUtils.sendMessage(event.getChannel(), "Already in a voice channel, either join that channel or wait for them to finish.");
            }

            // Turn the args back into a string separated by space
            String searchStr = String.join(" ", args);

            MusicHelper.loadAndPlay(event.getChannel(), searchStr);
            event.getMessage().delete();
        });

        musicCommands.put("stop", (event, args) -> {

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();

            if (botVoiceChannel == null)
                return;

            AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());

            audioP.clear();

            botVoiceChannel.leave();
            event.getMessage().delete();
        });

        // Skips the current song
        musicCommands.put("skip", (event, args) -> {
            MusicHelper.skipTrack(event.getChannel());
            event.getMessage().delete();
        });

        return musicCommands;
    }
}
