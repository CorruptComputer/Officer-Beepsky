package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildMusicManager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import java.util.List;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

public class SkipCommand implements Command {

  /**
   * Checks that the message was sent in a Guild, that command matches, and that the Guild has a
   * currently playing song.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null) {
      return false;
    }

    if (message.toLowerCase().equals(BotUtils.PREFIX + "next")
        || message.toLowerCase().equals(BotUtils.PREFIX + "n")
        || message.toLowerCase().equals(BotUtils.PREFIX + "skip")
        || message.toLowerCase().equals(BotUtils.PREFIX + "s")) {
      // if the bot is not in a voice channel ignore the commands
      try {
        guild.getMemberById(BotUtils.GATEWAY.getSelfId()).block().getVoiceState().block()
            .getChannel().block();
      } catch (NullPointerException e) {
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Skips to the next song in the queue for the Guild, if no song is available it stops playback.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    GuildMusicManager musicManager = getGuildMusicManager(guild.getId());
    List<AudioTrack> queue = musicManager.getScheduler().getQueue();

    if (queue.size() > 0) {
      BotUtils.sendMessage(channel, author, "Skipped to next track, now playing:",
          "[" + queue.get(0).getInfo().title + "](" + queue.get(0).getInfo().uri
              + ")" + " by " + queue.get(0).getInfo().author, Color.GREEN);

      musicManager.getScheduler().nextTrack();
    } else {
      MusicHelper.clearQueue(musicManager.getScheduler());
      BotUtils.sendMessage(channel, author, "Skipped to next track, nothing left to play!", "",
          Color.RED);
    }
  }

  /**
   * Returns the usage string for the SkipCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "skip`, `" + BotUtils.PREFIX + "s`, `"
        + BotUtils.PREFIX + "next`, or `" + BotUtils.PREFIX + "n` - Skips the current song.";
  }
}
