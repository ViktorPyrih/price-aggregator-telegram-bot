package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.search;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.Command;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.parser.BotCommandParser;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.FlowService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.ProductTelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Base64Utils;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand.SEARCH;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.SEARCH_FLOW_ID;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Command(SEARCH)
@RequiredArgsConstructor
public class SearchCommand implements BotCommand {

    private final FlowService flowService;
    private final BotCommandParser botCommandParser;
    private final PriceAggregatorService priceAggregatorService;
    private final ProductTelegramSenderService productTelegramSenderService;

    @Override
    public Result execute(Update update) throws TelegramApiException {
        long chatId = getChatId(update);

        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var arguments = botCommandParser.parseArguments(data);
            String query = Base64Utils.decodeAsString(arguments[1]);
            productTelegramSenderService.sendProducts(chatId, arguments[0], query, () -> priceAggregatorService.search(arguments[0], query));
        } else {
            flowService.start(SEARCH_FLOW_ID, update);
        }

        return Result.empty();
    }
}
