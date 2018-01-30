package com.nickolasgupton.beepsky.game;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class GameCommands implements Command {

  private static String PREFIX = "$";

  @Override
  public boolean shouldExecute(MessageReceivedEvent event) {
    return event.getMessage().getContent().startsWith(PREFIX);
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    String[] command = event.getMessage().getContent().split(" ");
    switch (command[0].substring(PREFIX.length()).toLowerCase()) {
      case "8ball":
        EightBall.ball(event);
        break;
      default:
        break;
    }
  }

  /**
   * Sends the available game commands to the recipient.
   * @param recipient Who the help message(s) should be sent to.
   */
  @Override
  public void getCommands(IPrivateChannel recipient) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("Game Commands:");
    builder.withDescription(
        "`" + PREFIX
            + "8ball` or `"
            + PREFIX
            + "8ball <question>` - Gives the answer you may not be looking for.\n");
    builder.withFooterText("v" + BotUtils.VERSION);
    RequestBuffer.request(() -> recipient.sendMessage(builder.build()));
  }
}

