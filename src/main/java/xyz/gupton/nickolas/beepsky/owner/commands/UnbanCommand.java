package xyz.gupton.nickolas.beepsky.owner.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel.Type;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import xyz.gupton.nickolas.beepsky.BotUtils;
import xyz.gupton.nickolas.beepsky.Command;
import xyz.gupton.nickolas.beepsky.owner.Owner;

public class UnbanCommand implements Command {

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
    if (guild == null && author.getId().equals(Owner.USER)) {
      if (message.split(" ").length != 2) {
        return false;
      }
      return (message.toLowerCase().startsWith("unban"));
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
    Snowflake userId = Snowflake.of(message.split(" ", 2)[1]);

    if (!BotUtils.isBanned(userId.asString())) {
      Owner.sendMessage("Error Unbanning", BotUtils.CLIENT.getUserById(userId).block().getUsername()
          + " is not banned.");
      return;
    }

    try {
      File file = new File("banned.txt");
      List<String> out = Files.lines(file.toPath())
          .filter(line -> !line.contains(userId.asString()))
          .collect(Collectors.toList());
      Files.write(file.toPath(), out, StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING);

      Owner
          .sendMessage("Unban Successful", BotUtils.CLIENT.getUserById(userId).block().getUsername()
              + " has been unbanned.");
    } catch (Exception e) {
      e.printStackTrace();
      Owner.sendMessage("Error Unbanning", e.getMessage());
    }
  }

  /**
   * Returns the usage string for a commands.
   *
   * @param recipient User, who command is going to, used for permissions checking.
   * @return String, the correct usage for the command.
   */
  @Override
  public String getCommand(User recipient) {
    if (recipient.getId().equals(Owner.USER)) {
      return "`unban <Discord ID>` - Unbans the user with that Discord ID from using this bot.";
    }

    return "";
  }
}
