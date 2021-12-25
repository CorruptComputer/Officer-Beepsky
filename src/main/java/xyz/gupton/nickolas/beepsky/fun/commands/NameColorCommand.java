package xyz.gupton.nickolas.beepsky.fun.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.RoleCreateSpec;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import java.util.List;
import java.util.regex.Pattern;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

/**
 * Command to allow a user to set the color of their name in a server.
 */
public class NameColorCommand implements Command {

  private static final Pattern hexPattern = Pattern.compile("^#[0-9a-fA-F]{6}$");

  /**
   * Checks if the command was sent in a Guild, if the command matches,
   * and if the bot has permission to edit roles.
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

    // Verify the command is correct
    String[] command = message.split(" ", 2);
    if (!command[0].equalsIgnoreCase(BotUtils.PREFIX + "namecolor")) {
      return false;
    }

    // Verify that they gave a hex code
    if (command.length != 2) {
      BotUtils.sendMessage(channel, author, "No color specified!", "Example: #FFFFFF",
          Color.RED);
      return false;
    }

    // Verify the hex pattern is correct
    if (!hexPattern.matcher(command[1]).matches()) {
      BotUtils.sendMessage(channel, author, "Color must be in hex format!", "Example: #FFFFFF",
          Color.RED);
      return false;
    }

    // Check if the bot has permissions to manage roles
    boolean permission = false;
    Member self = guild.getMemberById(BotUtils.GATEWAY.getSelfId()).block();
    if (self != null) {
      List<Role> selfRoles = self.getRoles().collectList().block();
      if (selfRoles != null) {
        for (Role role : selfRoles) {
          if (role.getPermissions().contains(Permission.MANAGE_ROLES)) {
            permission = true;
            break;
          }
        }
      }
    }

    return permission;
  }

  /**
   * Changes the color of a users name to the one specified by the message.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    String hexColor = message.split(" ", 2)[1].toUpperCase();
    Member member = guild.getMemberById(author.getId()).block();
    if (member == null) {
      return;
    }

    // remove the current color role if they have one
    List<Role> currentRoles = member.getRoles().collectList().block();
    if (currentRoles != null) {
      for (Role currentRole : currentRoles) {
        if (hexPattern.matcher(currentRole.getName()).matches()) {
          member.removeRole(currentRole.getId()).block();

          boolean empty = true;

          for (Member m : guild.getMembers().toIterable()) {
            if (!empty) {
              break;
            }

            for (Role r : m.getRoles().toIterable()) {
              if (currentRole == r) {
                empty = false;
                break;
              }
            }
          }

          // if that was the last user with the role, delete it
          if (empty) {
            currentRole.delete().block();
          }
        }
      }
    }


    Role role = null;
    for (Role r : guild.getRoles().toIterable()) {
      if (r.getName().equals(hexColor)) {
        role = r;
        break;
      }
    }

    // if it doesn't already exist we need to create a new one
    if (role == null) {
      role = guild.createRole(RoleCreateSpec.builder().name(hexColor)
              .color(Color.of(java.awt.Color.decode(hexColor).getRGB()))
              .hoist(false)
              .mentionable(false)
              .permissions(PermissionSet.none()).build()
      ).block();
    }

    // if it still doesn't exist something went wrong.
    if (role == null) {
      BotUtils.sendMessage(channel, author, "An error occurred while setting the color!",
          "Could not create the role required.", Color.RED);
      return;
    }

    member.addRole(role.getId()).block();

    BotUtils.sendMessage(channel, author, "Color sucessfully changed!", "", Color.GREEN);
  }

  /**
   * Returns the usage string for the NameColorCommand.
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
