package xyz.gupton.nickolas.beepsky.fun.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import reactor.core.publisher.Flux;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;

public class NameColorCommand implements Command {

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
    boolean permission = false;
    String[] hexColor = message.split(" ", 2);

    if (guild == null) {
      return false;
    }

    if (message.toLowerCase().startsWith(BotUtils.PREFIX + "namecolor")) {
      // Check if the bot has permissions to manage roles
      try {
        Flux<Role> selfRoles = Objects.requireNonNull(
            guild.getMemberById(BotUtils.GATEWAY.getSelfId()).block()
        ).getRoles();

        for (Role role : selfRoles.toIterable()) {
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

      if (hexColor.length != 2) {
        BotUtils.sendMessage(channel, author, "No color specified!", "Example: #FFFFFF",
            Color.RED);
        return false;
      }

      // if the name color specified is a valid hex code
      if (!Pattern.compile("^#(?:[0-9a-fA-F]{3}){1,2}$").matcher(hexColor[1]).matches()) {
        BotUtils.sendMessage(channel, author, "Color must be in hex format!", "Example: #FFFFFF",
            Color.RED);
        return false;
      }

      return true;
    }

    return false;
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

    // if it doesn't already exist we need to create a new one
    if (role == null) {
      role = guild.createRole(roleCreateSpec ->
          roleCreateSpec.setName(hexColor)
              .setColor(Color.of(java.awt.Color.decode(hexColor).getRGB())).setHoist(false)
              .setMentionable(false)
      ).block();
    }

    // if it still doesn't exist we need to try not to crash the bot
    if (role == null) {
      BotUtils.sendMessage(channel, author, "An error occurred while setting the color!",
          "If you see any code monkeys tell them: The role was null.", Color.RED);
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
