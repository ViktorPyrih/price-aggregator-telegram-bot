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
public class ChooseSubcategory2Step implements Step {

    private static final String CHOOSE_SUBCATEGORY_MESSAGE = "Choose a subcategory, please";
    private static final String WRONG_SUBCATEGORY_MESSAGE = "Please, choose one of the following subcategories: ";

    private final PriceAggregatorService priceAggregatorService;
    private final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return CHOOSE_SUBCATEGORY2_STEP_ID;
    }

    @Override
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        String subcategory = userState.getDataEntry(SUBCATEGORY);
        var subcategories = priceAggregatorService.getSubcategories(marketplace, category, subcategory);
        telegramSenderService.send(chatId, CHOOSE_SUBCATEGORY_MESSAGE, Buttons.keyboard(subcategories, true));

        return Result.of(userState);
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
            return Result.of(userState.nextStep().addDataEntry(SUBCATEGORY2, subcategory2));
        }

        telegramSenderService.send(chatId, WRONG_SUBCATEGORY_MESSAGE + String.join(", ", subcategories2));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        onStart(update, userState);
        return Result.of(userState
                .removeDataEntry(SUBCATEGORY2)
                .removeDataEntriesByPrefix(FILTER_KEY_PREFIX)
        );
    }
}
