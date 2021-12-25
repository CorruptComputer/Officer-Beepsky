package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

/**
 * Command for unbanning people from using the bot.
 */
public class UnbanCommand implements Command {

  /**
   * Checks the command and if it was sent in a PM to the bot.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   * @return boolean, true if the commands should be executed.
   */
  @Override
  public boolean shouldExecute(Guild guild, User author, MessageChannel channel, String message) {
    if (guild == null && author.getId().equals(Owner.OWNER_USER)) {
      if (message.split(" ").length != 2) {
        return false;
      }
      return (message.toLowerCase().startsWith("unban"));
    }

    return false;
  }

  /**
   * Unbans the user specified by the message.
   *
   * @param guild Guild, guild the message was received from, can be null for PM's.
   * @param author User, the author of the message.
   * @param channel MessageChannel, channel the message was received in.
   * @param message String, the contents of the message received.
   */
  @Override
  public void execute(Guild guild, User author, MessageChannel channel, String message) {
    Snowflake userId = Snowflake.of(message.split(" ", 2)[1]);
    User unbannedUser = BotUtils.GATEWAY.getUserById(userId).block();
    String username = unbannedUser != null ? unbannedUser.getUsername() : "'Unknown User'";

    if (!BotUtils.isBanned(userId.asString())) {
      Owner.sendMessage("Error Unbanning", username + " is not banned.");
      return;
    }

    try {
      File file = new File("banned.txt");
      List<String> out = Files.lines(file.toPath())
          .filter(line -> !line.contains(userId.asString()))
          .collect(Collectors.toList());
      Files.write(file.toPath(), out, StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING);

      Owner.sendMessage("Unban Successful", username + " has been unbanned.");
    } catch (Exception e) {
      e.printStackTrace();
      Owner.sendMessage("Error Unbanning", e.getMessage());
    }
  }

  /**
   * Returns the usage string for the UnbanCommand.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.OWNER_USER)) {
      return "`unban <Discord ID>` - Unbans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
