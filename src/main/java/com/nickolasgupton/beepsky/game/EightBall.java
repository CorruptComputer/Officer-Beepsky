package com.nickolasgupton.beepsky.game;

import com.nickolasgupton.beepsky.BotUtils;
import java.awt.Color;
import java.util.Random;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

class EightBall {

  /**
   * Random chance for either a positive, unsure, or negative answer.
   * @param author Author of the command
   * @param channel Author of the command
   * @param question Author of the command
   */
  public static void roll(IUser author, IChannel channel, String question) {
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

    BotUtils.sendMessage(channel, author, builder);
  }
}
