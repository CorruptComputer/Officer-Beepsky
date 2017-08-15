package com.grumpycode.polizziahut.commands;

import com.grumpycode.polizziahut.BotUtils;
import com.grumpycode.polizziahut.Command;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.Map;

public class GeneralCommands {
    public static Map<String, Command> getGeneralCommands() {
        Map<String, Command> generalCommands = new HashMap<>();

        generalCommands.put("help", (event, args) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withAuthorName("Grumpy Code");
            builder.withAuthorIcon("http://i.imgur.com/1lWPIgn.png");
            builder.withAuthorUrl("https://github.com/GrumpyCode");

            builder.withColor(255, 0, 0);
            builder.withTitle("__Command List__");
            builder.withDescription(
                    "**General Commands:**\n" +
                    "`" + BotUtils.DEFAULT_PREFIX + "help` - You should already know this one.\n" +
                    "`" + BotUtils.DEFAULT_PREFIX + "joinserver` - Bot will send you an invite to the Grumpy Code Discord server.\n\n"+
                    "**Music Commands:**\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "queue <YouTube/SoundCloud URL>` - Bot will play the song at that link.\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "stop` - Bot will clear the queue and leave the voice channel.\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "skip` - Bot will skip the currently playing song.\n\n");

            RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(builder.build()));
            event.getMessage().delete();

        });

        generalCommands.put("joinserver", (event, args) -> {
            RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage("https://discord.gg/pa9bssf"));
            event.getMessage().delete();
        });

        return generalCommands;
    }
}

