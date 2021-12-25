package xyz.gupton.nickolas.beepsky.music.commands;

import static xyz.gupton.nickolas.beepsky.music.MusicHelper.getGuildMusicManager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

/**
 * Command for changing the volume of the music being played.
 */
public class VolumeCommand implements Command {

  /**
   * Checks that the message was sent in a Guild, and that the command matches.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    int vol;
    String[] msg = message.split(" ");

    if (guild == null) {
      return false;
    }

    if (msg[0].equalsIgnoreCase(BotUtils.PREFIX + "volume")
        || msg[0].equalsIgnoreCase(BotUtils.PREFIX + "vol")) {

      // if the bot is not in a voice channel ignore the commands
      Member self = guild.getMemberById(BotUtils.GATEWAY.getSelfId()).block();
      if (self == null) {
        return false;
      }

      VoiceState selfVoiceState = self.getVoiceState().block();
      if (selfVoiceState == null) {
        return false;
      }

      VoiceChannel selfVoiceChannel = selfVoiceState.getChannel().block();
      if (selfVoiceChannel == null) {
        return false;
      }

      if (msg.length < 2) {
        BotUtils.sendMessage(channel, author, "No time given!", "", Color.RED);
        return false;
      }

      if (msg.length > 2) {
        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "This command only takes 1 argument, you provided: " + (msg.length - 1), Color.RED);

        return false;
      }

      try {
        vol = Integer.parseInt(msg[1]);
      } catch (NumberFormatException e) {
        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "The volume must be a number between 0 and 100", Color.RED);

        return false;
      }

      if (vol < 0 || vol > 100) {
        BotUtils.sendMessage(channel, author, "Error changing volume:",
            "The volume must be a number between 0 and 100", Color.RED);

        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Set the volume to the one specified in the message.
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

    BotUtils.sendMessage(channel, author, "The volume has been set to " + volume, "", Color.GREEN);
  }

  /**
   * Returns the usage string for the VolumeCommand.
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
