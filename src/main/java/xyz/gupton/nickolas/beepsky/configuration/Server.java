package xyz.gupton.nickolas.beepsky.configuration;

public class Server {
    private boolean isNameColorEnabled;
    private final String discordServerID;

    public Server(boolean isNameColorEnabled, String discordServerID) {
        this.isNameColorEnabled = isNameColorEnabled;
        this.discordServerID = discordServerID;
    }

    public Server(String discordServerID) {
        this(false, discordServerID);
    }
}
