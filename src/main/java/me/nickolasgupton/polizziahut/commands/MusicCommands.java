package me.nickolasgupton.polizziahut.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.nickolasgupton.polizziahut.Command;
import me.nickolasgupton.polizziahut.MusicHelper;
import me.nickolasgupton.polizziahut.lavaplayer.GuildMusicManager;
import me.nickolasgupton.polizziahut.lavaplayer.TrackScheduler;

import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.nickolasgupton.polizziahut.MusicHelper.getGuildAudioPlayer;

public class MusicCommands {
    public static Map<String, Command> getMusicCommands() {
        Map<String, Command> musicCommands = new HashMap<>();

        // Plays the first song found containing the first arg
        musicCommands.put("queue", (event, args) -> {
            event.getMessage().addReaction(":white_check_mark:");

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(255, 0, 0);
            builder.withTitle("Error queueing track:");
            builder.withFooterText(event.getAuthor().getName());

            if(event.getChannel().isPrivate()){
                builder.withDescription("Cannot queue music in a private chat.");
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
                return;
            }

            // user only messages "!!queue" with no track data
            if (args.size() == 0) {
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
            if (botVoiceChannel == null) {
                // clear the queue before joining
                TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();

                scheduler.getQueue().clear();
                scheduler.nextTrack();

                userVoiceChannel.join();
            } else
                // if the bot is currently in a voice channel that isn't the one that the user in in
                if (botVoiceChannel != userVoiceChannel) {
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

        // Lists the queue
        musicCommands.put("listqueue", (event, args) -> {
            event.getMessage().addReaction(":white_check_mark:");

            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());

            String str = MusicHelper.getQueue(musicManager.getScheduler().getQueue());

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(100, 255, 100);
            builder.withTitle("Next up:");
            builder.withDescription(str);

            builder.withFooterText(event.getAuthor().getName());

            int len = builder.getTotalVisibleCharacters();
            if(len > 2048){
                builder.withColor(255, 0, 0);
                builder.withTitle("Error listing queue:");
                builder.withDescription("Too long! Length: " + len);
            }else{
                RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            }
            event.getMessage().delete();
        });

        musicCommands.put("lq", musicCommands.get("listqueue")::runCommand);

        // Skips the current song
        musicCommands.put("skip", (event, args) -> {
            event.getMessage().addReaction(":white_check_mark:");

            GuildMusicManager musicManager = getGuildAudioPlayer(event.getChannel().getGuild());
            List<AudioTrack> queue = musicManager.getScheduler().getQueue();
            EmbedBuilder builder = new EmbedBuilder();

            builder.withColor(100, 255, 100);

            if (queue.size() > 0) {
                builder.withDescription("Skipped to next track, now playing:\n" +
                        "[" + queue.get(0).getInfo().title + "](" + queue.get(0).getInfo().uri + ")" + " by " + queue.get(0).getInfo().author);
            } else {
                builder.withDescription("Skipped to next track, nothing left to play.");
            }

            builder.withFooterText(event.getAuthor().getName());

            musicManager.getScheduler().nextTrack();
            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

            event.getMessage().delete();
        });

        musicCommands.put("stop", (event, args) -> {
            event.getMessage().addReaction(":white_check_mark:");

            IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getGuild()).getChannel();

            if (botVoiceChannel == null) return;

            TrackScheduler scheduler = getGuildAudioPlayer(event.getGuild()).getScheduler();

            scheduler.getQueue().clear();
            scheduler.nextTrack();
            botVoiceChannel.leave();

            event.getMessage().delete();
        });

        return musicCommands;
    }
}
