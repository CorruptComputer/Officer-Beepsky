package xyz.gupton.nickolas.beepsky.fun.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Permission;
import java.awt.Color;
import java.util.List;
import java.util.regex.Pattern;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class NameColorCommand implements Command {

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null) {
      return false;
    }

    if (message.toLowerCase().startsWith(BotUtils.PREFIX + "namecolor")) {
      // Check if the bot has permissions to manage roles
      boolean permission = false;

      try {
        for (Role role : guild.getMemberById(BotUtils.CLIENT.getSelf().block().getId()).block()
            .getRoles().toIterable()) {
          if (role.getPermissions().contains(Permission.MANAGE_ROLES)) {
            permission = true;
            break;
          }
        }
      } catch (NullPointerException e) {
        return false;
      }

      // if no permissions silently ignore the message
      if (!permission) {
        return false;
      }

      String hexColor = message.split(" ", 2)[1];

      // if the name color specified is a valid hex code
      if (!Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$").matcher(hexColor).matches()) {
        BotUtils.sendMessage(channel, author, "Color must be in hex format!", "Example: #FFFFFF",
            Color.red);
        return false;
      }

      return true;
    }

    return false;
  }

  /**
   * Checks things such as prefix and permissions to determine if a commands should be executed.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    String hexColor = message.split(" ", 2)[1];
    Member member = guild.getMemberById(author.getId()).block();

    try {
      List<Role> currentRoles = member.getRoles().collectList()
          .block();
      // remove the current color role
      for (Role role : currentRoles) {
        if (role.getName().startsWith("#")) {
          member.removeRole(role.getId());

          boolean empty = true;
          outer:
          for (Member m : guild.getMembers().toIterable()) {
            for (Role r : m.getRoles().toIterable()) {
              if (role == r) {
                empty = false;
                break outer;
              }
            }
          }
          // if that was the last user with the role, delete it
          if (empty) {
            role.delete().block();
          }
        }
      }
    } catch (NullPointerException e) {
      // do nothing
    }

    Role role = null;
    for (Role r : guild.getRoles().toIterable()) {
      if (r.getName().equals(hexColor)) {
        role = r;
        break;
      }
    }

    if (role == null) {
      role = guild.createRole(roleCreateSpec ->
          roleCreateSpec.setName(hexColor).setColor(Color.decode(hexColor)).setHoist(false)
              .setMentionable(false)
      ).block();
    }

    member.addRole(role.getId()).block();

    BotUtils.sendMessage(channel, author, "Color sucessfully changed!", "", Color.green);
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    return "`" + BotUtils.PREFIX
        + "namecolor <hex code>` - Changes your name's color to the one provided,"
        + " ignores the commands if not allowed by the server.";
  }
}
