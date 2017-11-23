package me.nickolasgupton.polizziahut.commands;

import me.nickolasgupton.polizziahut.BotUtils;
import me.nickolasgupton.polizziahut.Command;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.HashMap;
import java.util.Map;

public class OwnerCommands {
    public static Map<String, Command> getOwnerCommands() {
        Map<String, Command> ownerCommands = new HashMap<>();

        ownerCommands.put("shutdown", (event, args) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(100, 255, 100);
            builder.withTitle("Shutting down...");
            builder.withDescription("Goodbye world.");
            builder.withFooterText("Current version: " + BotUtils.VERSION);
            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

            System.exit(0);
        });

        ownerCommands.put("restart", (event, args) -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.withColor(100, 255, 100);
            builder.withTitle("Restarting...");
            builder.withDescription("This may take up to a few minutes if an update is available.");
            builder.withFooterText("Current version: " + BotUtils.VERSION);
            RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));

            System.exit(1);
        });

        return ownerCommands;
    }
}

