package me.nickolasgupton.polizziahut;

import me.nickolasgupton.polizziahut.lavaplayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHelper {

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private static final Map<Long, GuildMusicManager> playerInstances = new HashMap<>();

    public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = playerInstances.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            playerInstances.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    public static void loadAndPlay(final IChannel channel, final String trackUrl, final String authorName) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);

        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(100, 255, 100);
        builder.withFooterText(authorName);

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                builder.withTitle("Adding to queue:");
                builder.withDescription("[" + track.getInfo().title + "](" + trackUrl + ")" + " by " + track.getInfo().author);

                RequestBuffer.request(() -> channel.sendMessage(builder.build()));
                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                play(musicManager, firstTrack);

                // the queue for the playlist will start at the linked video
                for (int i = playlist.getTracks().indexOf(firstTrack) + 1; i < playlist.getTracks().size(); i++) {
                    play(musicManager, playlist.getTracks().get(i));
                }

                String str = getQueue(getGuildAudioPlayer(channel.getGuild()).getScheduler().getQueue());

                // message with the first song
                builder.withTitle("Adding playlist to queue:");
                builder.withDescription(playlist.getName() + "\n\n" +
                        "**First track:** " + "[" + firstTrack.getInfo().title + "](" + firstTrack.getInfo().uri + ")\n\n" +
                        "**Next up:**\n" + str);
                RequestBuffer.request(() -> channel.sendMessage(builder.build()));
            }

            @Override
            public void noMatches() {
                builder.withColor(255, 0, 0);
                builder.withTitle("Error queueing track:");
                builder.withDescription("Nothing found at URL: " + trackUrl);

                RequestBuffer.request(() -> channel.sendMessage(builder.build()));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                builder.withColor(255, 0, 0);
                builder.withTitle("Error queueing track:");
                builder.withDescription("Could not play track: " + exception.getMessage());

                RequestBuffer.request(() -> channel.sendMessage(builder.build()));
            }
        });
    }

    private static void play(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.getScheduler().queue(track);
    }

    public static String getQueue(List<AudioTrack> queue) {

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < queue.size(); i++) {

            // I hate the way this looks, but Intellij says its faster than string concatenation
            str.append((i + 1)).append(". [").append(queue.get(i).getInfo().title)
                    .append("](").append(queue.get(i).getInfo().uri)
                    .append(") by ").append(queue.get(i).getInfo().author).append("\n");

            // discord has a character limit of 2048, lets leave an extra 100 just to be safe
            if (i == 14 || str.length() == 1948) {
                str.append("+ ").append((queue.size() - i)).append(" more songs.");
                break;
            }
        }

        if (str.toString().equals("")) {
            str.append("Nothing currently queued.");
        }

        return str.toString();
    }
}
