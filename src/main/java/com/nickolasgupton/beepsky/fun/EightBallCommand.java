package com.nickolasgupton.beepsky.fun;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.awt.Color;
import java.util.Random;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class EightBallCommand implements Command {

  /**
   * Checks the prefix and command name to determine if it should be executed.
   * @param message The message received.
   * @return True if the command should be executed.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    return message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "8ball");
  }

  /**
   * Executes the command if it exists.
   * @param event Provided by D4J.
   */
  @Override
  public void execute(MessageReceivedEvent event) {
    String question = "";
    if (event.getMessage().toString().split(" ", 2).length > 1) {
      question = event.getMessage().toString().split(" ", 2)[1];
    }

    if (question.length() > 0 && !question.endsWith("?")) {
      question += "?  ";
    }

    EmbedBuilder builder = new EmbedBuilder();
    builder.withTitle(question + "8Ball says:");

    Random rdm = new Random();
    // gives them all an even chance, with rdm.nextInt(3) 0 is almost never picked
    int certainty = rdm.nextInt(30) % 3;
    switch (certainty) {
      case 0:
        builder.withColor(Color.green);
        break;
      case 1:
        builder.withColor(Color.orange);
        break;
      case 2:
        builder.withColor(Color.red);
        break;
      // hopefully never happens
      default:
        builder.withColor(0, 0, 0);
    }

    /*
      answers[0][x] = Positive
      answers[1][x] = Unsure
      answers[2][x] = Negative
    */
    String[][] answers = {
        {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely",
            "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes",
            "Signs point to yes",},
        {"Reply hazy try again", "Ask again later", "Better not tell you now",
            "Cannot predict now", "Concentrate and ask again"},
        {"Don't count on it", "My reply is no", "My sources say no", "Outlook not so good",
            "Very doubtful"}
    };

    builder.withDescription(answers[certainty][rdm.nextInt(answers[certainty].length)]);

    BotUtils.sendMessage(event.getChannel(), event.getAuthor(), builder);
  }

  /**
   * Returns the usage string for a command.
   * @return String of the correct usage for the command.
   */
  @Override
  public String getCommand(IUser recipient) {
    return  "`" + BotUtils.PREFIX + "8ball` or `" + BotUtils.PREFIX
        + "8ball <question>` - Gives the answer you may not be looking for.";
  }
}
