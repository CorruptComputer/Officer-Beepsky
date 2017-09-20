package me.nickolasgupton.polizziahut.commands;

import me.nickolasgupton.polizziahut.BotUtils;
import me.nickolasgupton.polizziahut.Command;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameCommands {

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
                    {"It is certain", "It is decidedly so", "Without a doubt", "Yes definitely", "You may rely on it", "As I see it, yes", "Most likely", "Outlook good", "Yes", "Signs point to yes",},
                    {"Reply hazy try again", "Ask again later", "Better not tell you now", "Cannot predict now", "Concentrate and ask again"},
                    {"Don't count on it", "My reply is no", "My sources say no", "Outlook not so good", "Very doubtful"}
            };

            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(100, 255, 100);

            if(args.size() > 0){
                StringBuilder question = new StringBuilder();

                for(String str: args){
                    question.append(str);
                }

                if(!question.toString().endsWith("?")) question.append("?");

                builder.withTitle(question.toString() + "  8Ball says:");
            }else{
                builder.withTitle("8Ball says:");
            }

            int certainty = rdm.nextInt(3);
            builder.withDescription(answers[certainty][rdm.nextInt(answers[certainty].length)]);

            builder.withFooterText(event.getAuthor().getName());

            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
            event.getMessage().delete();
        });

//        gameCommands.put("poker", (event, args) -> {
//        });

//        gameCommands.put("blackjack", (event, args) -> {
//        });

//        gameCommands.put("uno", (event, args) -> {
//        });

        return gameCommands;

    }
}
