package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ChooseCategoryStep implements Step {

    private static final String WRONG_MARKETPLACE_MESSAGE = "Please, choose one of the following marketplaces: ";
    private static final String CHOOSE_CATEGORY_MESSAGE = "Choose a category, please";

    private final PriceAggregatorService priceAggregatorService;
    private final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return CHOOSE_CATEGORY_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var marketplaces = priceAggregatorService.getMarketplaces();
        String marketplace = update.getMessage().getText();
        if (marketplaces.contains(marketplace)) {
            var categories = priceAggregatorService.getCategories(marketplace);
            telegramSenderService.send(chatId, CHOOSE_CATEGORY_MESSAGE, Buttons.keyboard(categories, true));

            return Result.of(userState.nextStep().addDataEntry(MARKETPLACE, marketplace));
        }

        telegramSenderService.send(chatId, WRONG_MARKETPLACE_MESSAGE + String.join(", ", marketplaces));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        var categories = priceAggregatorService.getCategories(marketplace);
        telegramSenderService.send(chatId, CHOOSE_CATEGORY_MESSAGE, Buttons.keyboard(categories, true));

        return Result.of(userState.nextStep().removeDataEntry(CATEGORY));
    }
}
