package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.basic;

import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.Command;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand.HELP;

@Command(HELP)
public class HelpCommand implements BotCommand {

    private static final String HELP = """
            <b>Help:</b>
                - /start - To get started with the bot
            """;

    @Override
    public BotCommand.Result execute(Update update) {
        return BotCommand.Result.builder()
                .response(HELP)
                .build();
    }
}
