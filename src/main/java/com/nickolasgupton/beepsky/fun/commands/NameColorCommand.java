package com.nickolasgupton.beepsky.fun.commands;

import com.nickolasgupton.beepsky.BotUtils;
import com.nickolasgupton.beepsky.Command;
import java.awt.Color;
import java.util.regex.Pattern;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.EmbedBuilder;

public class NameColorCommand implements Command {

  /**
   * Determines if the commands, prefix, and permissions are correct.
   * @param message The message received.
   * @return True if the commands is valid.
   */
  @Override
  public boolean shouldExecute(IMessage message) {
    if (message.getChannel().isPrivate()) {
      return false;
    }

    if (message.toString().toLowerCase().startsWith(BotUtils.PREFIX + "namecolor")) {
      // Check if the bot has permissions to manage roles
      boolean permission = false;
      for (IRole role : message.getGuild().getRolesForUser(BotUtils.CLIENT.getOurUser())) {
        if (role.getPermissions().contains(Permissions.MANAGE_PERMISSIONS)) {
          permission = true;
          break;
        }
      }

      // if no permissions silently ignore the message
      if (!permission) {
        return false;
      }

      String hexColor = message.toString().split(" ", 2)[1];
      EmbedBuilder embedBuilder = new EmbedBuilder();
      // if the name color specified is a valid hex code
      if (!Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$").matcher(hexColor).matches()) {
        embedBuilder.withColor(Color.red);
        embedBuilder.withTitle("Color must be in hex format!");
        embedBuilder.withDescription("Example: #FFFFFF");
        BotUtils.sendMessage(message.getAuthor().getOrCreatePMChannel(), message.getAuthor(),
            embedBuilder);
        return false;
      }

      return true;
    }

    return false;
  }

  @Override
  public void execute(MessageReceivedEvent event) {
    EmbedBuilder embedBuilder = new EmbedBuilder();

    String hexColor = event.getMessage().toString().split(" ", 2)[1];

    embedBuilder.withColor(Color.green);

    IRole role;
    if (event.getGuild().getRolesByName(hexColor).isEmpty()) {
      role = event.getGuild().createRole();
      embedBuilder.withTitle("Color successfully added!");
    } else {
      role = event.getGuild().getRolesByName(hexColor).get(0);
      embedBuilder.withTitle("Color successfully updated!");
    }

    role.changeName(hexColor);

    role.changeColor(Color.decode(hexColor));

    role.changeHoist(false);
    role.changeMentionable(false);

    event.getAuthor().addRole(role);

    BotUtils.sendMessage(event.getAuthor().getOrCreatePMChannel(), event.getAuthor(), embedBuilder);
  }

  @Override
  public String getCommand(IUser recipient) {
    return "`" + BotUtils.PREFIX
        + "namecolor <hex code>` - Changes your name's color to the one provided,"
        + " ignores the commands if not allowed by the server.";
  }
}
