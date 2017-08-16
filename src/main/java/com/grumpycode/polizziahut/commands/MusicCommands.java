package com.grumpycode.polizziahut.commands;

import com.grumpycode.polizziahut.BotUtils;
import com.grumpycode.polizziahut.Command;
import com.grumpycode.polizziahut.MusicHelper;
import com.grumpycode.polizziahut.lavaplayer.GuildMusicManager;
import com.grumpycode.polizziahut.lavaplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.grumpycode.polizziahut.MusicHelper.getGuildAudioPlayer;

public class MusicCommands {
    public static Map<String, Command> getMusicCommands() {
        Map<String, Command> musicCommands = new HashMap<>();

        // Plays the first song found containing the first arg
        musicCommands.put("queue", (event, args) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withFooterText(event.getAuthor().getName());

            // user only messages "!!queue" with no track data
            if(args.size() == 0){
                builder.withDescription("No track specified.");
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                event.getMessage().delete();
                return;
            }

            IVoiceChannel userVoiceChannel = event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel();

            // user is not in a voice channel
            if (userVoiceChannel == null) {
                builder.withDescription("Not in a voice channel, join one and then try again.");
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                event.getMessage().delete();
                return;
            }

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();
            // if the bot is not currently in a voice channel, join the user
            if(botVoiceChannel == null){
                // clear the queue before joining
                TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();

                scheduler.getQueue().clear();
                scheduler.nextTrack();

                userVoiceChannel.join();
            }else
                // if the bot is currently in a voice channel that isn't the one that the user in in
                if(botVoiceChannel != userVoiceChannel){
                builder.withDescription("Already in a voice channel, either join that channel or wait for them to finish.");
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                event.getMessage().delete();
                return;
            }

            // Turn the args back into a string separated by space
            String searchStr = String.join(" ", args);
            MusicHelper.loadAndPlay(event.getChannel(), searchStr, event.getAuthor().getName());
            event.getMessage().delete();
        });

        musicCommands.put("q", musicCommands.get("queue")::runCommand);

        musicCommands.put("stop", (event, args) -> {

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();

            if (botVoiceChannel == null)
                return;

            TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();

            scheduler.getQueue().clear();
            scheduler.nextTrack();

            botVoiceChannel.leave();
        });

        // Skips the current song
        musicCommands.put("skip", (event, args) -> {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            builder.withDescription("Skipped to next track.");

            builder.withFooterText(event.getAuthor().getName());

            musicManager.getScheduler().nextTrack();
            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

            event.getMessage().delete();
        });

        // Skips the current song
        musicCommands.put("listqueue", (event, args) -> {
            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());

            String str = MusicHelper.getQueue(musicManager.getScheduler().getQueue());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            builder.withTitle("Next up:");
            builder.withDescription(str);

            builder.withFooterText(event.getAuthor().getName());

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            event.getMessage().delete();
        });

        musicCommands.put("lq", musicCommands.get("listqueue")::runCommand);

        return musicCommands;
    }
}
