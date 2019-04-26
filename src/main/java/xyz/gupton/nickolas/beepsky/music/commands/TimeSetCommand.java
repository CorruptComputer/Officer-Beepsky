package xyz.gupton.nickolas.beepsky.music.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.awt.Color;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;

public class TimeSetCommand implements Command {

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

    if (message.toLowerCase().startsWith(BotUtils.PREFIX + "timeset")
        || message.startsWith(BotUtils.PREFIX + "ts")) {

      // if the bot is not in a voice channel ignore the commands
      try {
        guild.getMemberById(BotUtils.CLIENT.getSelfId().get()).block().getVoiceState().block()
            .getChannel().block();
      } catch (NullPointerException e) {
        return false;
      }

      // split the ;time and <time> from each other
      String[] msg = message.split(" ", 2);

      if (msg.length < 2) {
        BotUtils.sendMessage(channel, author, "No time given!", "", Color.red);
        return false;
      }

      // Regex for pattern matching of HH:MM:SS with parts being optional
      Pattern pattern = Pattern.compile("(\\d{1,2}:)?([0-5]?\\d:)?[0-5]?\\d");

      if (!pattern.matcher(msg[1]).matches()) {
        BotUtils
            .sendMessage(channel, author, "Incorrect formatting for the time, try `[HH:][MM:]SS`. "
                + "The `HH:` and `MM:` are optional.", "", Color.red);
        return false;
      }

      return true;
    } else {
      return false;
    }
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
    long lengthOfTrack = MusicHelper.getGuildMusicManager(guild.getId()).getScheduler()
        .getPlayingSong()
        .getDuration();

    String[] time = message.split(" ")[1].split(":");

    long timeToSet = 0;

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

    if (timeToSet > lengthOfTrack) {
      BotUtils.sendMessage(channel, author, "The time specified is after the track ends!", "", Color.red);
    }

    MusicHelper.getGuildMusicManager(guild.getId()).getScheduler().getPlayingSong()
        .setPosition(timeToSet);

    BotUtils.sendMessage(channel, author, "The time has been set to " + message.split(" ")[1], "", Color.green);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "time <time>` or `"
        + BotUtils.PREFIX + "t <time>` - Sets the time to the time specified, usage: \n"
        + "`[HH:][MM:]SS`. The `HH:` and `MM:` are optional.";
  }
}
