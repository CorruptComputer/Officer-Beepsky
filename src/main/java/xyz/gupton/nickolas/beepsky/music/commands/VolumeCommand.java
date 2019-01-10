package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildMusicManager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import java.awt.Color;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class VolumeCommand implements Command {

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

    if (message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "volume")
        || message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "vol")) {
      String[] msg = message.toString().split(" ");

      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(Color.red);
      builder.withTitle("Error changing volume:");

      if (msg.length > 2) {

        builder.withDescription("This command only takes 1 argument, you provided: "
            + (msg.length - 1));

        BotUtils.sendMessage(message.getChannel(), message.getAuthor(), builder);
        return false;
      }

      int vol;

      try {
        vol = Integer.parseInt(msg[1]);
      } catch (NumberFormatException e) {
        builder.withDescription("The volume must be a number between 0 and 100");

        BotUtils.sendMessage(message.getChannel(), message.getAuthor(), builder);
        return false;
      }

      if (vol < 0 || vol > 100) {
        builder.withDescription("The volume must be a number between 0 and 100");

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
    AudioPlayer musicManager = getGuildMusicManager(event.getGuild()).getAudioPlayer();
    int volume = Integer.parseInt(event.getMessage().toString().split(" ")[1]);

    musicManager.setVolume(volume);

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);
    builder.withTitle("The volume has been set to " + volume);
    BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @return String of the correct usage for the commands.
   */
  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "volume <volume>` or `"
        + BotUtils.PREFIX + "vol <volume>` - Sets the volume for the audio streamed, number between 0 and 100.";
  }
}
