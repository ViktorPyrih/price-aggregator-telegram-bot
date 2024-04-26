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
        return CHOOSE_SUBCATEGORY_STEP_ID;
    }

    @Override
    public void onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);

        var subcategories = priceAggregatorService.getSubcategories(marketplace, category);
        telegramSenderService.send(chatId, CHOOSE_SUBCATEGORY_MESSAGE, Buttons.keyboard(subcategories, true));
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);

        var subcategories = priceAggregatorService.getSubcategories(marketplace, category);
        String subcategory = update.getMessage().getText();

        if (subcategories.contains(subcategory)) {
            return Result.of(userState.nextStep().addDataEntry(SUBCATEGORY, subcategory));
        }

        telegramSenderService.send(chatId, WRONG_SUBCATEGORY_MESSAGE + String.join(", ", subcategories));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        onStart(update, userState);
        return Result.of(userState.removeDataEntry(SUBCATEGORY));
    }
}
