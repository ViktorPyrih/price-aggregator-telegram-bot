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
public class ChooseSubcategoryStep implements Step {

    private static final String WRONG_CATEGORY_MESSAGE = "Please, choose one of the following categories: ";
    private static final String CHOOSE_SUBCATEGORY_MESSAGE = "Choose a subcategory, please";

    private final PriceAggregatorService priceAggregatorService;
    private final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return CHOOSE_SUBCATEGORY_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);

        var categories = priceAggregatorService.getCategories(marketplace);
        String category = update.getMessage().getText();

        if (categories.contains(category)) {
            var subcategories = priceAggregatorService.getSubcategories(marketplace, category);
            telegramSenderService.send(chatId, CHOOSE_SUBCATEGORY_MESSAGE, Buttons.keyboard(subcategories, true));

            return Result.of(userState.nextStep().addDataEntry(CATEGORY, category));
        }

        telegramSenderService.send(chatId, WRONG_CATEGORY_MESSAGE + String.join(", ", categories));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        var subcategories = priceAggregatorService.getSubcategories(marketplace, category);
        telegramSenderService.send(chatId, CHOOSE_SUBCATEGORY_MESSAGE, Buttons.keyboard(subcategories, true));

        return Result.of(userState.nextStep().removeDataEntry(SUBCATEGORY));
    }
}
