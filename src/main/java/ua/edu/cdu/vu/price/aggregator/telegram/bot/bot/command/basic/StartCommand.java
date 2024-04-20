package ua.edu.cdu.vu.event.notification.telegram.bot.component.command.impl.basic;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.Command;

@Command(BotCommand.START)
public class StartCommand implements BotCommand {

    private static final String GREETING = "Hi, %s. My name is %s. How can I help you?";

    @Value("${bot.configuration.username}")
    private String botName;

    @Override
    public BotCommand.Result execute(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        return BotCommand.Result.builder()
                .response(GREETING.formatted(username, botName))
                .build();
    }
}
