package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.search;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.Command;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.FlowService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand.SEARCH;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.QUERY;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.SEARCH_FLOW_ID;

@Command(SEARCH)
@RequiredArgsConstructor
public class SearchCommand implements BotCommand {

    private final FlowService flowService;

    @Override
    public Result execute(Update update) throws TelegramApiException {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            flowService.start(SEARCH_FLOW_ID, update, state -> state.addDataEntry(QUERY, data));
        } else {
            flowService.start(SEARCH_FLOW_ID, update);
        }

        return Result.empty();
    }
}
