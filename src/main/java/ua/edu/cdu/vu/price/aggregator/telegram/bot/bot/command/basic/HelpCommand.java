package ua.edu.cdu.vu.event.notification.telegram.bot.component.command.impl.basic;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.Command;

import static ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand.HELP;

@Command(HELP)
public class HelpCommand implements BotCommand {

    private static final String HELP = """
            <b>Help:</b>
                - /start - To get started with the bot
                - /create - To create an event
                - /all - To get a list of all events
            """;

    @Override
    public BotCommand.Result execute(Update update) throws TelegramApiException {
        return BotCommand.Result.builder()
                .response(HELP)
                .build();
    }
}
