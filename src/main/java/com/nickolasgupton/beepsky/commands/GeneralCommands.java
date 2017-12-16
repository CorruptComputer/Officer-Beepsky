package com.nickolasgupton.beepsky.commands;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.Map;

public class GeneralCommands {
    public static Map<String, Command> getGeneralCommands() {
        Map<String, Command> generalCommands = new HashMap<>();

        generalCommands.put("help", (event, args) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(100, 255, 100);
            builder.withTitle("__Command List__");
            builder.withDescription(
                    "**General Commands:**\n" +
                    "`" + BotUtils.DEFAULT_PREFIX + "help` - You should already know this one.\n\n" +
                    "**Music Commands:**\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "queue <(YouTube/SoundCloud URL)/YouTube search>` or `" + BotUtils.MUSIC_PREFIX +"q <(YouTube/SoundCloud URL)/YouTube search>` - Plays the song at that link.\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "listqueue` or `" + BotUtils.MUSIC_PREFIX +"lq` - Messages back a list of the current queue.\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "skip` - Skips the current song.\n" +
                    "`" + BotUtils.MUSIC_PREFIX + "stop` - Clears the current queue and leaves the voice channel.\n\n" +
                    "**Game Commands:**\n" +
                    "`" + BotUtils.GAME_PREFIX + "8ball` or `" + BotUtils.GAME_PREFIX +"8ball <question>` - Gives the answer you may not be looking for.\n\n" +
                    "Officer-Beepsky is an open source Discord bot, you can view the source here on [GitHub](https://github.com/CorruptComputer/Officer-Beepsky).");

            builder.withFooterText("v" + BotUtils.VERSION);

            RequestBuffer.request(() -> event.getAuthor().getOrCreatePMChannel().sendMessage(builder.build()));
            event.getMessage().delete();

        });

        return generalCommands;
    }
}

