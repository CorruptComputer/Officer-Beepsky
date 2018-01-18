package com.nickolasgupton.beepsky.music;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class MusicCommands implements Command {

  private static String PREFIX = "!!";

  @Override
  public boolean shouldExecute(MessageReceivedEvent event) {
    return !event.getChannel().isPrivate() && event.getMessage().getContent().startsWith(PREFIX);
  }

  @Override
  public void execute(MessageReceivedEvent event) {

    String[] command = event.getMessage().getContent().split(" ");
    switch (command[0].substring(PREFIX.length()).toLowerCase()) {
      case "queue":
      case "q":
        Queue.addToQueue(event);
        break;
      case "listqueue":
      case "lq":
        Queue.listQueue(event);
        break;
      case "skip":
      case "next":
        Queue.nextSong(event);
        break;
      case "stop":
        Queue.stop(event);
        break;
      default:
        break;
    }
  }

  @Override
  public void getCommands(MessageReceivedEvent event) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("Music Commands:");
    builder.withDescription(
        "`" + PREFIX
            + "queue <song>`\n"
            + "or `" + PREFIX
            + "q <song>` - Song can be in the form of either a YouTube URL, SoundCloud URL,"
            + " or if it is not a URL it will search from YouTube.\n"

            + "`" + PREFIX
            + "listqueue` "
            + "or `" + PREFIX
            + "lq` - Messages back a list of the current queue.\n"

            + "`" + PREFIX
            + "skip` "
            + "or `" + PREFIX
            + "next` - Skips the current song.\n"

            + "`" + PREFIX
            + "stop` - Clears the current queue and leaves the voice channel.\n");
    builder.withFooterText("v" + BotUtils.VERSION);
    RequestBuffer
        .request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(builder.build()));
  }
}
