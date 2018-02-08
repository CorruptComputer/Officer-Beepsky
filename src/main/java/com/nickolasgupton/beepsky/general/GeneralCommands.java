package com.nickolasgupton.beepsky.general;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;

import java.awt.Color;
import java.util.ServiceLoader;
import java.util.regex.Pattern;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class GeneralCommands implements Command {

  private static String PREFIX = ".";

  @Override
  public boolean shouldExecute(IMessage message) {
    return message.getContent().startsWith(PREFIX);
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    String[] command = event.getMessage().getContent().split(" ");
    switch (command[0].substring(PREFIX.length()).toLowerCase()) {
      case "help":
        help(event.getAuthor().getOrCreatePMChannel());
        event.getMessage().delete();
        break;
      case "namecolor":
        if (command.length > 1) {
          if (changeNameColor(event.getAuthor(), event.getGuild(), command[1])) {
            event.getMessage().delete();
          }
        }
        break;
      default:
        break;
    }


  }

  /**
   * Sends the available general commands to the recipient.
   * @param recipient Who the help message(s) should be sent to.
   */
  @Override
  public void getCommands(IPrivateChannel recipient) {
    EmbedBuilder builder = new EmbedBuilder();
    builder.withColor(100, 255, 100);
    builder.withTitle("General Commands:");
    builder.withDescription("`" + PREFIX
        + "help` - You should already know this one.\n"

        + "`" + PREFIX
        + "namecolor <hex code>` - Changes your name's color to the one provided,"
        + " ignores the command if not allowed by the server.\n\n"

        + "Officer-Beepsky is an open source Discord bot, you can view the source here on [GitHub](https://github.com/CorruptComputer/Officer-Beepsky).");

    builder.withFooterText("v" + BotUtils.VERSION);
    RequestBuffer.request(() -> recipient.sendMessage(builder.build()));
  }

  /**
   * Changes the users color for the guild.
   * @param author Author of the request
   * @param guild Guild the request was sent in
   * @param hexColor Hex code for the color
   * @return Returns true if the message should be deleted
   */
  private static boolean changeNameColor(IUser author, IGuild guild, String hexColor) {
    boolean permission = false;
    for (IRole role : guild.getRolesForUser(BotUtils.CLIENT.getOurUser())) {
      if (role.getPermissions().contains(Permissions.MANAGE_PERMISSIONS)) {
        permission = true;
        break;
      }
    }

    // if no permissions silently ignore the message
    if (!permission) {
      return false;
    }

    EmbedBuilder embedBuilder = new EmbedBuilder();

    // if the name color specified is a valid hex code
    if (!Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$").matcher(hexColor).matches()) {
      embedBuilder.withColor(Color.red);
      embedBuilder.withTitle("Color must be in hex format!");
      embedBuilder.withDescription("Example: #FFFFFF or #FFF");
      RequestBuffer.request(() -> author.getOrCreatePMChannel().sendMessage(embedBuilder.build()));
      return true;
    }

    embedBuilder.withColor(Color.green);

    IRole role;
    if (guild.getRolesByName(author.getStringID()).isEmpty()) {
      role = guild.createRole();
      embedBuilder.withTitle("Color successfully added!");
    } else {
      role = guild.getRolesByName(author.getStringID()).get(0);
      embedBuilder.withTitle("Color successfully updated!");
    }

    role.changeName(author.getStringID());

    role.changeColor(Color.decode(hexColor));

    role.changeHoist(false);
    role.changeMentionable(false);

    author.addRole(role);

    RequestBuffer.request(() -> author.getOrCreatePMChannel().sendMessage(embedBuilder.build()));
    return true;
  }

  /**
   * Sends all available commands to the recipient.
   * @param recipient Who the help message(s) should be sent to.
   */
  private static void help(IPrivateChannel recipient) {
    ServiceLoader<Command> serviceLoader = ServiceLoader.load(Command.class);
    for (Command commands : serviceLoader) {
      commands.getCommands(recipient);
    }
  }
}

