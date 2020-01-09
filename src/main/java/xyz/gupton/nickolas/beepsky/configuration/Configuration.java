package xyz.gupton.nickolas.beepsky.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Configuration {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Scanner input = new Scanner(System.in);

    private final File botConfigJSONFile;
    private Bot bot;

    private final File userConfigJSONFile;
    private HashMap<String, User> users;

    private final File serverConfigJSONFile;
    private HashMap<String, Server> servers;

    public Configuration() {
        // Gets the file path for $USER_HOME/.config/Officer-Beepsky/
        File configDirectory = new File(System.getProperty("user.home")
                + File.separator + ".config" + File.separator + "Officer-Beepsky");

        if (!configDirectory.exists()) {
            configDirectory.mkdirs();
        }

        // Gets the file path for $USER_HOME/.config/Officer-Beepsky/botConfig.json
        botConfigJSONFile = new File(System.getProperty("user.home")
                + File.separator + ".config" + File.separator + "Officer-Beepsky" + File.separator + "botConfig.json");

        if (!botConfigJSONFile.exists()) {
            try {
                botConfigJSONFile.createNewFile();
                System.out.println("Please enter the Discord Bot Token: ");
                String token = input.nextLine();
                System.out.println("Please enter the Discord ID for the Owner: ");
                String owner = input.nextLine();
                bot = new Bot(token, owner);

                saveBotConfig();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            try {
                FileReader reader = new FileReader(botConfigJSONFile);
                bot = gson.fromJson(reader, new TypeToken<Bot>(){}.getType());
                reader.close();

                if (bot.getDiscordBotToken().isEmpty()) {
                    System.out.println("Please enter the Discord Bot Token: ");
                    String token = input.nextLine();
                    bot = new Bot(token, bot.getOwnerDiscordID());
                }

                if (bot.getOwnerDiscordID().isEmpty()) {
                    System.out.println("Please enter the Discord ID for the Owner: ");
                    String owner = input.nextLine();
                    bot = new Bot(bot.getDiscordBotToken(), owner);
                }


            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }

        }

        // Gets the file path for $USER_HOME/.config/Officer-Beepsky/userConfig.json
        userConfigJSONFile = new File(System.getProperty("user.home")
                + File.separator + ".config" + File.separator + "Officer-Beepsky" + File.separator + "userConfig.json");

        if (!userConfigJSONFile.exists()) {
            try {

                userConfigJSONFile.createNewFile();
                users = new HashMap<String, User>();

                getUser(bot.getOwnerDiscordID()).setAdmin(true);

                saveUserConfig();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            try {
                FileReader reader = new FileReader(userConfigJSONFile);
                users = gson.fromJson(reader, new TypeToken<HashMap<String, User>>(){}.getType());
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

        // Gets the file path for $USER_HOME/.config/Officer-Beepsky/serverConfig.json
        serverConfigJSONFile = new File(System.getProperty("user.home")
                + File.separator + ".config" + File.separator + "Officer-Beepsky" + File.separator + "serverConfig.json");

        if (!serverConfigJSONFile.exists()) {
            try {
                serverConfigJSONFile.createNewFile();
                servers = new HashMap<String, Server>();

                saveServerConfig();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            try {
                FileReader reader = new FileReader(userConfigJSONFile);
                servers = gson.fromJson(reader, new TypeToken<HashMap<String, Server>>(){}.getType());
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }


    }

    public User getUser(String discordID) {
        if (users.containsKey(discordID)) {
            return users.get(discordID);
        }

        // If the user doesn't exist, create them and return that object.
        users.put(discordID, new User(discordID));
        saveUserConfig();

        return users.get(discordID);
    }

    public Server getServer(String discordServerID) {
        if (servers.containsKey(discordServerID)) {
            return servers.get(discordServerID);
        }

        // If the user doesn't exist, create them and return that object.
        servers.put(discordServerID, new Server(discordServerID));
        saveServerConfig();

        return servers.get(discordServerID);
    }

    public String getDiscordToken() {
        return bot.getDiscordBotToken();
    }

    public String getOwner() {
        return bot.getOwnerDiscordID();
    }

    public void saveBotConfig() {
        try {
            FileWriter writer = new FileWriter(botConfigJSONFile);
            writer.write(gson.toJson(bot, new TypeToken<Bot>(){}.getType()));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void saveUserConfig() {
        try {
            FileWriter writer = new FileWriter(userConfigJSONFile);
            writer.write(gson.toJson(users, new TypeToken<HashMap<String, User>>(){}.getType()));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void saveServerConfig() {
        try {
            FileWriter writer = new FileWriter(serverConfigJSONFile);
            writer.write(gson.toJson(users, new TypeToken<HashMap<String, Server>>(){}.getType()));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
