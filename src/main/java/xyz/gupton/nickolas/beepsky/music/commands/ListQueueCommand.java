package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildMusicManager;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import java.awt.Color;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;


public class ListQueueCommand implements Command {

  /**
   * Checks that the message was sent in a Guild and that the command matches.
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

    return (message.toLowerCase().equals(BotUtils.PREFIX + "listqueue")
        || message.toLowerCase().equals(BotUtils.PREFIX + "lq"));
  }

  /**
   * Gets the queue of songs in a Guild and sends it back in a list.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    GuildMusicManager musicManager = getGuildMusicManager(guild.getId());
    AudioTrackInfo playingInfo = musicManager.getScheduler().getPlayingSong().getInfo();

    BotUtils.sendMessage(channel, author, "Current queue:", "Now playing: " + "["
        + playingInfo.title + "](" + playingInfo.uri + ") by " + playingInfo.author + "\n\n"
        + MusicHelper.queueToString(musicManager.getScheduler().getQueue()), Color.green);
  }

  /**
   * Returns the usage string for the ListQueueCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "listqueue` or `"
        + BotUtils.PREFIX + "lq` - Messages back a list of the current queue.";
  }
}
