package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildMusicManager;

import java.awt.Color;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class VolumeCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
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

    if (message.toLowerCase().startsWith(BotUtils.PREFIX + "volume")
        || message.toLowerCase().startsWith(BotUtils.PREFIX + "vol")) {

      // if the bot is not in a voice channel ignore the commands
      try {
        guild.getMemberById(BotUtils.CLIENT.getSelfId().get()).block().getVoiceState().block()
            .getChannel().block();
      } catch (NullPointerException e) {
        return false;
      }

      String[] msg = message.split(" ");

      if (msg.length > 2) {

        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "This command only takes 1 argument, you provided: " + (msg.length - 1), Color.red);

        return false;
      }

      int vol;

      try {
        vol = Integer.parseInt(msg[1]);
      } catch (NumberFormatException e) {
        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "The volume must be a number between 0 and 100", Color.red);

        return false;
      }

      if (vol < 0 || vol > 100) {
        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "The volume must be a number between 0 and 100", Color.red);

        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    AudioPlayer musicManager = getGuildMusicManager(guild.getId()).getAudioPlayer();
    int volume = Integer.parseInt(message.split(" ")[1]);

    musicManager.setVolume(volume);

    BotUtils.sendMessage(channel, author, "The volume has been set to " + volume, "", Color.green);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "volume <volume>` or `"
        + BotUtils.PREFIX
        + "vol <volume>` - Sets the volume for the audio streamed, number between 0 and 100.";
  }
}
