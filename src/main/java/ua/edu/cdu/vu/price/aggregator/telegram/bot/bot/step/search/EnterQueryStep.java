package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.ProductTelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.SEARCH_FLOW_ID;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class EnterQueryStep implements Step {

    private static final String ENTER_QUERY_MESSAGE = "Enter query, please";

    private final TelegramSenderService telegramSenderService;
    private final PriceAggregatorService priceAggregatorService;
    private final ProductTelegramSenderService productTelegramSenderService;

    @Override
    public int flowId() {
        return SEARCH_FLOW_ID;
    }

    @Override
    public int stepId() {
        return ENTER_QUERY_STEP_ID;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        telegramSenderService.sendMessage(chatId, ENTER_QUERY_MESSAGE, true);

        return Result.of(userState);
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        String query = update.getMessage().getText();

        productTelegramSenderService.sendProducts(chatId, query, () -> priceAggregatorService.search(query));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        return onStart(update, userState);
    }

}
