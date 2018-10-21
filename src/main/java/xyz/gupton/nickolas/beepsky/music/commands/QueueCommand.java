package xyz.gupton.nickolas.beepsky.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.util.regex.Pattern;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

public class QueueCommand implements Command {

  /**
   * Checks prefix, commands name, and if there is a track provided.
   *
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      return false;
    }

    if (message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "queue")
        || message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "q")) {
      String[] split = message.toString().toLowerCase().split(" ", 2);

      // user messages just "!q" with no track info
      if (split.length == 1) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(255, 0, 0);
        builder.withTitle("Error queueing track:");
        builder.withDescription("No track specified.");
        BotUtils.sendMessage(message.getChannel(), message.getAuthor(), builder);
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Executes the commands if it exists.
   *
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    String song = event.getMessage().toString().split(" ", 2)[1];

    if (!(song.startsWith("ytsearch:") || song.startsWith("scsearch:"))
        // RegEx shamelessly copied from:
        // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
        && !Pattern
        .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        .matcher(song).matches()) {
      song = "ytsearch:" + song;
    }

    IVoiceChannel userVoiceChannel =
        event.getAuthor().getVoiceStateForGuild(event.getChannel().getGuild()).getChannel();

    // user is not in a voice channel
    if (userVoiceChannel == null) {
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(255, 0, 0);
      builder.withTitle("Error queueing track:");
      builder.withDescription("You are not in a voice channel.");
      BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
      return;
    }

    IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
        .getVoiceStateForGuild(event.getGuild()).getChannel();

    // if the bot is not currently in a voice channel, join the user
    if (botVoiceChannel == null) {
      userVoiceChannel.join();
    } else {
      // if the bot is currently in a voice channel that isn't the one that the user in in
      if (botVoiceChannel != userVoiceChannel) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(255, 0, 0);
        builder.withTitle("Error queueing track:");
        builder.withDescription("Music player currently in use with another channel,"
            + " either join that one or wait for them to finish.");
        BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
        return;
      }
    }

    AudioSourceManagers.registerRemoteSources(MusicHelper.playerManager);
    AudioSourceManagers.registerLocalSource(MusicHelper.playerManager);
    GuildMusicManager musicManager = MusicHelper.getGuildAudioPlayer(event.getGuild());

    final String track = song;
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);
    MusicHelper.playerManager
        .loadItemOrdered(musicManager, track, new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            builder.withTitle("Adding to queue:");
            builder.withDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                + " by " + track.getInfo().author);

            BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
            musicManager.getScheduler().queue(track);
          }

          @Override
          public void playlistLoaded(AudioPlaylist playlist) {
            // if it is a search vs an actual playlist
            if (track.startsWith("ytsearch:") || track.startsWith("scsearch:")) {

              builder.withTitle("Adding to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "[" + playlist.getTracks().get(0).getInfo().title + "]("
                  + playlist.getTracks().get(0).getInfo().uri + ")" + " by "
                  + playlist.getTracks().get(0).getInfo().author);
              musicManager.getScheduler().queue(playlist.getTracks().get(0));
            } else {
              AudioTrack firstTrack = playlist.getSelectedTrack();

              if (firstTrack == null) {
                firstTrack = playlist.getTracks().get(0);
              }

              musicManager.getScheduler().queue(firstTrack);

              // the queue for the playlist will start at the linked video
              for (int i = playlist.getTracks().indexOf(firstTrack) + 1;
                  i < playlist.getTracks().size(); i++) {
                musicManager.getScheduler().queue(playlist.getTracks().get(i));
              }

              String str = MusicHelper
                  .queueToString(MusicHelper.getGuildAudioPlayer(event.getGuild())
                      .getScheduler().getQueue());

              // message with the first song
              builder.withTitle("Adding playlist to queue:");
              builder.withDescription(playlist.getName() + "\n\n"
                  + "**First track:** " + "[" + firstTrack.getInfo().title + "]("
                  + firstTrack.getInfo().uri + ")\n\n"
                  + str);
            }

            BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
          }

          @Override
          public void noMatches() {
            builder.withColor(Color.red);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Nothing found at: " + track);

            BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            builder.withColor(Color.red);
            builder.withTitle("Error queueing track:");
            builder.withDescription("Could not play track: " + exception.getMessage());

            BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
          }
        });
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "queue <song>` or `"
        + BotUtils.PREFIX + "q <song>` - Song can be in the form of either a YouTube URL, "
        + "SoundCloud URL, or if it is not a URL it will search from YouTube.";
  }
}
