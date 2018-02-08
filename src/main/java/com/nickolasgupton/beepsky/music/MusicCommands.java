package com.nickolasgupton.beepsky.music;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.util.regex.Pattern;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.EmbedBuilder;

public class MusicCommands implements Command {

  private static String PREFIX = "!!";

  @Override
  public boolean shouldExecute(IMessage message) {
    return !message.getChannel().isPrivate() && message.getContent().startsWith(PREFIX);
  }

  @Override
  public void execute(MessageReceivedEvent event) {

    String[] command = event.getMessage().getContent().split(" ", 2);
    switch (command[0].substring(PREFIX.length()).toLowerCase()) {
      case "queue":
      case "q":
        EmbedBuilder builder = new EmbedBuilder();
        builder.withColor(255, 0, 0);
        builder.withTitle("Error queueing track:");
        builder.withFooterText(event.getAuthor().getDisplayName(event.getChannel().getGuild()));

        if (command.length == 1) {
          builder.withDescription("No track specified.");
        } else {
          // if it does not already contain a search keyword, and is a not a URL
          if (!(command[1].startsWith("ytsearch:") || command[1].startsWith("scsearch:"))
              // RegEx shamelessly copied from:
              // https://stackoverflow.com/questions/163360/regular-expression-to-match-urls-in-java
              && !Pattern
              .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
              .matcher(command[1]).matches()) {
            command[1] = "ytsearch:" + command[1];
          }

          switch (Queue.addToQueue(event.getAuthor(), event.getChannel(), command[1])) {
            case NOT_IN_VOICE:
              builder.withDescription("Not in a voice channel, join one and then try again.");
              break;
            case ALREADY_IN_USE:
              builder.withDescription("Already in use, join that voice channel or wait until they"
                  + " are finished.");
              break;
            default:
              // no error, no message.
              return;
          }
        }

        BotUtils.sendMessage(event.getChannel(), builder.build());
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
    event.getMessage().delete();
  }

  /**
   * Sends the available music commands to the recipient.
   *
   * @param recipient Who the help message(s) should be sent to.
   */
  @Override
  public void getCommands(IPrivateChannel recipient) {
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
    BotUtils.sendMessage(recipient, builder.build());
  }
}
