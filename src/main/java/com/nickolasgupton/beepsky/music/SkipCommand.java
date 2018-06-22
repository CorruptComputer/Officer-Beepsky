package com.nickolasgupton.beepsky.music;

import static com.nickolasgupton.beepsky.music.MusicHelper.getGuildAudioPlayer;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import java.util.List;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

public class SkipCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a command should be executed.
   *
   * @param message The message received.
   * @return True if the command should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {

    if (message.getChannel().isPrivate()) {
      return false;
    }

    if (getGuildAudioPlayer(message.getGuild()).getScheduler().getPlayingSong() == null) {
      return false;
    }

    return (message.toString().toLowerCase().equals(BotUtils.PREFIX + "next")
        || message.toString().toLowerCase().equals(BotUtils.PREFIX + "n")
        || message.toString().toLowerCase().equals(BotUtils.PREFIX + "skip")
        || message.toString().toLowerCase().equals(BotUtils.PREFIX + "s"));
  }

  /**
   * Executes the command if it exists.
   *
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());
    List<AudioTrack> queue = musicManager.getScheduler().getQueue();
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);

    if (queue.size() > 0) {
      builder.withTitle("Skipped to next track, now playing:");
      builder.withDescription("[" + queue.get(0).getInfo().title + "](" + queue.get(0).getInfo().uri
          + ")" + " by " + queue.get(0).getInfo().author);
      musicManager.getScheduler().nextTrack();
      BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
    } else {
      IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
          .getVoiceStateForGuild(event.getGuild()).getChannel();
      botVoiceChannel.leave();

      EmbedBuilder message = new EmbedBuilder();
      message.withColor(Color.green);
      message.withTitle("Skipped to next track, nothing left to play!");
      BotUtils.sendMessage(event.getChannel(), event.getAuthor(), message);
    }
  }

  /**
   * Returns the usage string for a command.
   *
   * @return String of the correct usage for the command.
   */
  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "skip`, `" + BotUtils.PREFIX + "s`, `"
        + BotUtils.PREFIX + "next`, or `" + BotUtils.PREFIX + "n` - Skips the current song.";
  }
}
