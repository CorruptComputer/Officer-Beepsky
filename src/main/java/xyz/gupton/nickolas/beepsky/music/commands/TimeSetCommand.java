package xyz.gupton.nickolas.beepsky.music.commands;

import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.music.MusicHelper;
import java.awt.Color;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;

public class TimeSetCommand implements Command {

  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      return false;
    }

    if (message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "timeset")
        || message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "ts")) {
      IVoiceChannel botVoiceChannel = BotUtils.CLIENT.getOurUser()
          .getVoiceStateForGuild(message.getGuild()).getChannel();

      // if the bot is not in a voice channel ignore the commands
      if (botVoiceChannel == null) {
        return false;
      }

      // split the ;time and <time> from each other
      String[] msg = message.toString().split(" ", 2);

      if (msg.length < 2) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(Color.red);
        builder.withTitle("No time given!");
        BotUtils.sendMessage(message.getChannel(), message.getAuthor(), builder);
        return false;
      }

      // Regex for pattern matching of HH:MM:SS with parts being optional
      Pattern pattern = Pattern.compile("(\\d{1,2}:)?([0-5]?\\d:)?[0-5]?\\d");

      if (!pattern.matcher(msg[1]).matches()) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(Color.red);
        builder.withTitle("Incorrect formatting for the time, try `[HH:][MM:]SS`. "
            + "The `HH:` and `MM:` are optional.");
        BotUtils.sendMessage(message.getChannel(), message.getAuthor(), builder);
        return false;
      }

      return true;
    } else {
      return false;
    }
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    long lengthOfTrack = MusicHelper.getGuildAudioPlayer(event.getGuild())
        .getScheduler().getPlayingSong().getDuration();

    String[] time = event.getMessage().toString().split(" ")[1].split(":");

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
      EmbedBuilder builder = new EmbedBuilder();
      builder.withColor(Color.red);
      builder.withTitle("The time specified is after the track ends!");
      BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
    }

    MusicHelper.getGuildAudioPlayer(event.getGuild())
        .getScheduler().getPlayingSong().setPosition(timeToSet);

    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(Color.green);
    builder.withTitle("The time has been set to " + event.getMessage().toString().split(" ")[1]);
    BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
  }

  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX + "time <time>` or `"
        + BotUtils.PREFIX + "t <time>` - Sets the time to the time specified, usage: \n"
        + "`[HH:][MM:]SS`. The `HH:` and `MM:` are optional.";
  }
}
