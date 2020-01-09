package xyz.gupton.nickolas.beepsky;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.User;

class CommandHandler {

  /**
   * Called when a messaged is received by the bot, whether it be private or in a server.
   *
   * @param event Provided by Discord4j.
   */
  static void onMessageReceived(MessageCreateEvent event) {
    Guild guild = event.getGuild().blockOptional().orElse(null);
    User author = event.getMessage().getAuthor().orElse(null);
    MessageChannel channel = event.getMessage().getChannel().block();
    String message = event.getMessage().getContent().orElse(null);

    if (author == null
        || message == null
        || author.isBot()
        || BotUtils.isBanned(author.getId().asString())) {
      return;
    }

    // Search all available commands types
    for (Command command : Globals.commands) {

      // Check if the provided message should be executed
      if (command.shouldExecute(guild, author, channel, message)) {

        command.execute(guild, author, channel, message);

        //TODO: replace this with ScheduledExecutorService
        //Timer timer = new Timer();

        //timer.schedule(new TimerTask() {
        //  @Override
        //  public void run() {
        //    event.getMessage().delete().block();
        //  }
        //}, TimeUnit.SECONDS.toMillis(10));
      }
    }
  }
}
