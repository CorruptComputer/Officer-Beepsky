package xyz.gupton.nickolas.beepsky.configuration;

public class User {
    private boolean isAdmin;
    private boolean isBanned;
    private final String discordID;

    public User(boolean isAdmin, boolean isBanned, String discordID) {
        this.discordID = discordID;
        this.isAdmin = false;
        this.isBanned = false;
    }

    public User(String discordID) {
        this(false, false, discordID);
    }

    public boolean isBanned() {
        return this.isBanned;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getDiscordID() {
        return this.discordID;
    }
}
