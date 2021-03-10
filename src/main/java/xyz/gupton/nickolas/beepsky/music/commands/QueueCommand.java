package xyz.gupton.nickolas.beepsky.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import java.util.regex.Pattern;
import reactor.core.publisher.Mono;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

public class QueueCommand implements Command {

  /**
   * Checks if the message was sent in a Guild
   * and if the command matches the requirements to queue the song.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    String[] split = message.split(" ", 2);

    if (guild == null) {
      return false;
    }

    if (message.toLowerCase().startsWith(BotUtils.PREFIX + "queue")
        || message.toLowerCase().startsWith(BotUtils.PREFIX + "q")) {

      // If no track info is provided don't continue.
      if (split.length == 1) {
        BotUtils.sendMessage(channel, author, "Error queueing track:", "No track specified.",
            Color.RED);
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Queue's the song provided by the message for the Guild provided,
   * and joins the authors VoiceChannel.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    // Setup variables
    String song = message.split(" ", 2)[1];
    GuildMusicManager musicManager = MusicHelper.getGuildMusicManager(guild.getId());
    final String track;

    // If the song matches a search string or a video URL its good to go,
    // otherwise prepend the ytsearch: string to it.
    if ((song.startsWith("ytsearch:") || song.startsWith("scsearch:"))
        // RegEx shamelessly copied from:
        // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
        && Pattern
        .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        .matcher(song).matches()) {
      track = song;
    } else {
      track = "ytsearch:" + song;
    }

    Member guildMember = guild.getMemberById(author.getId()).block();
    if (guildMember == null)  {
      BotUtils.sendMessage(channel, author, "Error queueing track:",
          "You do not exist.", Color.RED);
      return;
    }

    VoiceState guildMemberVoiceState = guildMember.getVoiceState().block();
    if (guildMemberVoiceState == null)  {
      BotUtils.sendMessage(channel, author, "Error queueing track:",
          "You are not in a voice channel.", Color.RED);
      return;
    }

    VoiceChannel userVoiceChannel = guildMemberVoiceState.getChannel().block();
    if (userVoiceChannel == null) {
      BotUtils
          .sendMessage(channel, author, "Error queueing track:", "You are not in a voice channel.",
              Color.RED);

      return;
    }

    // If the bot is in a different voice channel than the user don't continue.
    Member self = guild.getMemberById(BotUtils.GATEWAY.getSelfId()).block();
    if (self == null) {
      BotUtils.sendMessage(channel, author, "Error queueing track:",
          "I do not exist.", Color.RED);
      return;
    }

    VoiceState selfVoiceState = self.getVoiceState().block();
    VoiceChannel botVoiceChannel = null;
    if (selfVoiceState != null) {
      botVoiceChannel = selfVoiceState.getChannel().block();
      if (botVoiceChannel != null && !botVoiceChannel.getId().equals(userVoiceChannel.getId())) {
        BotUtils.sendMessage(channel, author, "Error queueing track:",
            "Music player currently in use with another channel, "
                + "either join that one or wait for them to finish.", Color.RED);
        return;
      }
    }

    // Join the user if the bot is not already in there.
    if (botVoiceChannel == null) {
      musicManager.setBotVoiceConnection(userVoiceChannel.join(spec ->
          spec.setProvider(musicManager.getAudioProvider())).block());
    }

    AudioSourceManagers.registerRemoteSources(MusicHelper.playerManager);
    AudioSourceManagers.registerLocalSource(MusicHelper.playerManager);
    MusicHelper.playerManager
        .loadItemOrdered(musicManager, track, new AudioLoadResultHandler() {
          @Override
          public void trackLoaded(AudioTrack track) {
            BotUtils.sendMessage(channel, author, "Adding to queue:",
                "[" + track.getInfo().title + "](" + track.getInfo().uri + ")"
                    + " by " + track.getInfo().author, Color.GREEN);

            musicManager.getScheduler().queue(track);
          }

          @Override
          public void playlistLoaded(AudioPlaylist playlist) {
            // if it is a search vs an actual playlist
            if (track.startsWith("ytsearch:") || track.startsWith("scsearch:")) {

              BotUtils.sendMessage(channel, author, "Adding to queue:",
                  playlist.getName() + "\n\n" + "["
                      + playlist.getTracks().get(0).getInfo().title + "]("
                      + playlist.getTracks().get(0).getInfo().uri + ")" + " by "
                      + playlist.getTracks().get(0).getInfo().author, Color.GREEN);

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

              String str = MusicHelper.queueToString(musicManager.getScheduler().getQueue());

              // message with the first song
              BotUtils.sendMessage(channel, author, "Adding playlist to queue:",
                  playlist.getName() + "\n\n" + "**First track:** " + "["
                      + firstTrack.getInfo().title + "](" + firstTrack.getInfo().uri + ")\n\n"
                      + str, Color.GREEN);
            }

          }

          @Override
          public void noMatches() {
            BotUtils.sendMessage(channel, author, "Error queueing track:",
                "Nothing found at: " + track, Color.RED);
          }

          @Override
          public void loadFailed(FriendlyException exception) {
            BotUtils.sendMessage(channel, author, "Error queueing track:",
                "Could not play track: " + exception.getMessage(), Color.RED);
          }
        });
  }

  /**
   * Returns the usage string for the QueueCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "queue <song>` or `"
        + BotUtils.PREFIX + "q <song>` - Song can be in the form of either a YouTube URL, "
        + "SoundCloud URL, or if it is not a URL it will search from YouTube.";
  }
}
