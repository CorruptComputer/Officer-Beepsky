package xyz.gupton.nickolas.beepsky.configuration;

public class Bot {
    private final String discordBotToken;
    private final String ownerDiscordID;

    public Bot(String discordBotToken, String ownerDiscordID) {
        this.discordBotToken = discordBotToken;
        this.ownerDiscordID = ownerDiscordID;
    }

    public String getDiscordBotToken() {
        return this.discordBotToken;
    }

    public String getOwnerDiscordID() {
        return this.ownerDiscordID;
    }
}
