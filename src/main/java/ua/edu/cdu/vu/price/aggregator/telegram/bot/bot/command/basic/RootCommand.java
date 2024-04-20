package ua.edu.cdu.vu.event.notification.telegram.bot.component.command.impl.basic;

import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.BotCommand;
import ua.edu.cdu.vu.event.notification.telegram.bot.component.command.Command;

@Command(BotCommand.ROOT)
public class RootCommand implements BotCommand {

    private static final String COMMAND_NOT_SUPPORTED = "Command '%s' is not supported!";

    @Override
    public BotCommand.Result execute(Update update) {
        return BotCommand.Result.builder()
                .response(COMMAND_NOT_SUPPORTED.formatted(update.getMessage().getText()))
                .build();
    }
}
