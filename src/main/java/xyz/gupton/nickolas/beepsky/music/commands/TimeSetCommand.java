package xyz.gupton.nickolas.beepsky.music.commands;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.util.Color;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

/**
 * Command to set the time in the currently playing song.
 */
public class TimeSetCommand implements Command {

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
    // Regex for pattern matching of HH:MM:SS with parts being optional
    Pattern pattern = Pattern.compile("(\\d{1,2}:)?([0-5]?\\d:)?[0-5]?\\d");
    // split the ;time and <time> from each other
    String[] msg = message.split(" ", 2);

    if (guild == null) {
      return false;
    }

    if (msg[0].equalsIgnoreCase(BotUtils.getInstance().PREFIX + "timeset")
        || msg[0].equalsIgnoreCase(BotUtils.getInstance().PREFIX + "ts")) {

      // if the bot is not in a voice channel ignore the commands
      Member self = guild.getMemberById(BotUtils.getInstance().GATEWAY.getSelfId()).block();
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
        BotUtils.getInstance().sendMessage(channel, author, "No time given!", "", Color.RED);
        return false;
      }

      if (!pattern.matcher(msg[1]).matches()) {
        BotUtils.getInstance()
            .sendMessage(channel, author, "Incorrect formatting for the time, try `[HH:][MM:]SS`. "
                + "The `HH:` and `MM:` are optional.", "", Color.RED);
        return false;
      }

      return true;
    }

    return false;

  }

  /**
   * Sets the time for the current song to the time specified in the message.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    String[] time = message.split(" ")[1].split(":");
    long timeToSet = 0;
    long lengthOfCurrentTrack = MusicHelper.getGuildMusicManager(guild.getId()).getScheduler()
        .getPlayingSong()
        .getDuration();

    // Has hours set.
    if (time.length > 2) {
      timeToSet += TimeUnit.HOURS.toMillis(Long.parseLong(time[0]));
      timeToSet += TimeUnit.MINUTES.toMillis(Long.parseLong(time[1]));
      timeToSet += TimeUnit.SECONDS.toMillis(Long.parseLong(time[2]));
      // Minutes.
    } else if (time.length > 1) {
      timeToSet += TimeUnit.MINUTES.toMillis(Long.parseLong(time[0]));
      timeToSet += TimeUnit.SECONDS.toMillis(Long.parseLong(time[1]));
    } else {
      timeToSet += TimeUnit.SECONDS.toMillis(Long.parseLong(time[0]));
    }

    if (timeToSet > lengthOfCurrentTrack) {
      BotUtils.getInstance().sendMessage(channel, author, "The time specified is after the track ends!", "",
          Color.RED);
    }

    MusicHelper.getGuildMusicManager(guild.getId()).getScheduler().getPlayingSong()
        .setPosition(timeToSet);

    BotUtils.getInstance().sendMessage(channel, author, "The time has been set to " + message.split(" ")[1], "",
        Color.GREEN);
  }

  /**
   * Returns the usage string for the TimeSetCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.getInstance().PREFIX + "time <time>` or `"
        + BotUtils.getInstance().PREFIX + "t <time>` - Sets the time to the time specified, usage: \n"
        + "`[HH:][MM:]SS`. The `HH:` and `MM:` are optional.";
  }
}
