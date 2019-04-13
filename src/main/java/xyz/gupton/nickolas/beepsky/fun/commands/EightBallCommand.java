package xyz.gupton.nickolas.beepsky.fun.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import java.awt.Color;
import java.util.Random;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class EightBallCommand implements Command {

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
    return message.toLowerCase().startsWith(BotUtils.PREFIX + "8ball");
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
    String question = "";
    if (message.split(" ", 2).length > 1) {
      question = message.split(" ", 2)[1];
    }

    if (question.length() > 0 && !question.endsWith("?")) {
      question += "?";
    }

    Random rdm = new Random();
    // gives them all an even chance, with rdm.nextInt(3) 0 is almost never picked
    int certainty = rdm.nextInt(30) % 3;

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
    Color[] color = {Color.green, Color.orange, Color.red};

    BotUtils.sendMessage(channel, author, question + " | 8Ball says:",
        answers[certainty][rdm.nextInt(answers[certainty].length)], color[certainty]);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX + "8ball` or `" + BotUtils.PREFIX
        + "8ball <question>` - Gives the answer you may not be looking for.";
  }
}
