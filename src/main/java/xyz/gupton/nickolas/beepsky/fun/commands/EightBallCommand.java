package xyz.gupton.nickolas.beepsky.fun.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import java.util.Random;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

/**
 * Command to shake the 8 ball.
 */
public class EightBallCommand implements Command {

  private static Random rdm = new Random();

  /**
   * Checks the message for the correct command.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    return message.toLowerCase().startsWith(BotUtils.getInstance().PREFIX + "8ball");
  }

  /**
   * Determines the fate of the user who sent the message.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    String question = "";
    // gives them all a more even chance, with rdm.nextInt(3) 0 is almost never picked
    int certainty = rdm.nextInt(300) % 3;
    /*
      Most of these are generic 8ball quotes, but some are from here: https://github.com/tgstation/tgstation/tree/master/sound/voice/beepsky
      answers[0][x] = Positive
      answers[1][x] = Unsure
      answers[2][x] = Negative
    */
    String[][] answers = {
        {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely",
            "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes",
            "Signs point to yes", "Have a secure day"},
        {"Reply hazy try again", "Ask again later", "Better not tell you now",
            "Cannot predict now", "Concentrate and ask again", "I am the law",
            "God made tomorrow for the crooks we don't catch today", "You can't out run a radio"},
        {"Don't count on it", "My reply is no", "My sources say no", "Outlook not so good",
            "Very doubtful", "Criminal detected", "Prepare for justice", "Freeze scumbag"}
    };
    Color[] color = {Color.GREEN, Color.ORANGE, Color.RED};

    if (message.split(" ", 2).length > 1) {
      question = message.split(" ", 2)[1];
    }

    if (question.length() > 0 && !question.endsWith("?")) {
      question += "?";
    }

    BotUtils.getInstance().sendMessage(channel, author, question + " | 8Ball says:",
        answers[certainty][rdm.nextInt(answers[certainty].length)], color[certainty]);
  }

  /**
   * Returns the usage string for the EightBallCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.getInstance().PREFIX + "8ball` or `" + BotUtils.getInstance().PREFIX
        + "8ball <question>` - Gives the answer you may not be looking for.";
  }
}
