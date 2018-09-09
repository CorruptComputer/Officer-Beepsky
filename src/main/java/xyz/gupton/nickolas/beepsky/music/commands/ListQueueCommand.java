package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildAudioPlayer;

import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.GuildMusicManager;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.awt.Color;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class ListQueueCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param message The message received.
   * @return True if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      return false;
    }

    return (message.toString().toLowerCase().equals(BotUtils.PREFIX + "listqueue")
        || message.toString().toLowerCase().equals(BotUtils.PREFIX + "lq"));
  }

  /**
   * Executes the commands if it exists.
   *
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    GuildMusicManager musicManager = getGuildAudioPlayer(event.getGuild());

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);
    builder.withTitle("Current queue:");
    AudioTrackInfo playingInfo = musicManager.getScheduler().getPlayingSong().getInfo();
    builder.withDescription("Now playing: " + "[" + playingInfo.title + "](" + playingInfo.uri
        + ") by " + playingInfo.author + "\n\n");

    builder.appendDescription(MusicHelper.queueToString(musicManager.getScheduler().getQueue()));

    BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "listqueue` or `"
        + BotUtils.PREFIX + "lq` - Messages back a list of the current queue.";
  }
}
