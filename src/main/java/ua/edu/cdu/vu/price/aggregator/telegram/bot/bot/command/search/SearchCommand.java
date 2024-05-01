package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.search;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.Command;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.FlowService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.SEARCH_FLOW_ID;

@Command("/search")
@RequiredArgsConstructor
public class SearchCommand implements BotCommand {

    private final FlowService flowService;

    @Override
    public Result execute(Update update) throws TelegramApiException {
        flowService.start(SEARCH_FLOW_ID, update);
        return Result.empty();
    }
}
