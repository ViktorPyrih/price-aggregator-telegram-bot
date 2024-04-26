package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class ChooseFilterKeyStep extends FilterStep {

    private static final String WRONG_SUBCATEGORY_MESSAGE = "Please, choose one of the following subcategories: ";
    private static final String CHOOSE_FILTER_MESSAGE = "Choose a filter, please";

    private final TelegramSenderService telegramSenderService;

    public ChooseFilterKeyStep(PriceAggregatorService priceAggregatorService, TelegramSenderService telegramSenderService) {
        super(priceAggregatorService);
        this.telegramSenderService = telegramSenderService;
    }

    @Override
    public int stepId() {
        return CHOOSE_FILTER_KEY_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        String subcategory = userState.getDataEntry(SUBCATEGORY);

        var subcategories2 = priceAggregatorService.getSubcategories(marketplace, category, subcategory);
        String subcategory2 = update.getMessage().getText();

        if (subcategories2.contains(subcategory2)) {
            var filters = priceAggregatorService.getFilters(marketplace, category, subcategory, subcategory2);
            telegramSenderService.send(chatId, CHOOSE_FILTER_MESSAGE, Buttons.keyboard(extractKeys(filters), true, true));

            return Result.of(userState.nextStep().addDataEntry(SUBCATEGORY2, subcategory2));
        }

        telegramSenderService.send(chatId, WRONG_SUBCATEGORY_MESSAGE + String.join(", ", subcategories2));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        telegramSenderService.send(chatId, CHOOSE_FILTER_MESSAGE, Buttons.keyboard(extractKeys(filters), true, true));

        return Result.of(userState.nextStep().removeDataEntry(FILTER_KEY));
    }
}
