package com.nickolasgupton.beepsky.commands;

import com.nickolasgupton.beepsky.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class GameCommands {

  /**
   * Game Commands are defined here.
   *
   * @return returns the map of Game Commands
   */
  public static Map<String, Command> getGameCommands() {
    Map<String, Command> gameCommands = new HashMap<>();

    gameCommands.put("8ball", (event, args) -> {
      Random rdm = new Random();

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

      EmbedBuilder builder = new EmbedBuilder();

      if (args.size() > 0) {
        StringBuilder question = new StringBuilder();

        boolean firstWord = true;
        for (String str : args) {
          if (!firstWord) {
            question.append(' ');
          } else {
            firstWord = false;
          }

          question.append(str);
        }

        if (!question.toString().endsWith("?")) {
          question.append("?");
        }

        builder.withTitle(question.toString() + "  8Ball says:");
      } else {
        builder.withTitle("8Ball says:");
      }

      // gives them all an even chance, with rdm.nextInt(3) 0 is almost never picked
      int certainty = rdm.nextInt(30) % 3;
      switch (certainty) {
        case 0:
          builder.withColor(100, 255, 100);
          break;
        case 1:
          builder.withColor(255, 255, 100);
          break;
        case 2:
          builder.withColor(255, 100, 100);
          break;
        // hopefully never happens
        default:
          builder.withColor(0, 0, 0);
      }

      builder.withDescription(answers[certainty][rdm.nextInt(answers[certainty].length)]);

      builder.withFooterText(event.getAuthor().getDisplayName(event.getGuild()));

      RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
      event.getMessage().delete();
    });

    return gameCommands;

  }
}
