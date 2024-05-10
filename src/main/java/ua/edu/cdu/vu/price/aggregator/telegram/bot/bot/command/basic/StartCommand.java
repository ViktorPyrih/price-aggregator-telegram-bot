package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.basic;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.Command;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserStateService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.FlowService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand.START;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.AGGREGATOR_FLOW_ID;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Command(START)
@RequiredArgsConstructor
public class StartCommand implements BotCommand {

    private static final String GREETING = "Hello, %s! Let's get started";

    private final FlowService flowService;
    private final TelegramSenderService telegramSenderService;
    private final UserStateService userStateService;

    @Override
    public BotCommand.Result execute(Update update) throws TelegramApiException {
        long chatId = getChatId(update);
        if (!userStateService.exists(chatId)) {
            String username = update.getMessage().getFrom().getFirstName();
            telegramSenderService.sendMessage(chatId, GREETING.formatted(username));
        }

        flowService.start(AGGREGATOR_FLOW_ID, update);

        return Result.empty();
    }
}
