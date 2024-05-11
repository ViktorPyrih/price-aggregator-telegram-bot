package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Marketplace;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import java.util.Map;

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
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        String marketplaceEntry = userState.getDataEntry(MARKETPLACE);

        var subcategories = userState.getAllDataEntriesByPrefix(SUBCATEGORY);

        Marketplace marketplace = priceAggregatorService.getMarketplaces().get(marketplaceEntry);
        if (subcategories.size() == marketplace.getSubcategoriesCount()) {
            return Result.of(userState.nextStep());
        }

        long chatId = getChatId(update);

        String category = userState.getDataEntry(CATEGORY);
        var nextSubcategories = priceAggregatorService.getSubcategories(marketplaceEntry, category, subcategories);
        if (nextSubcategories.isEmpty()) {
            return Result.of(userState.nextStep());
        }

        telegramSenderService.sendMessage(chatId, CHOOSE_SUBCATEGORY_MESSAGE, Buttons.keyboard(nextSubcategories, true));

        return Result.of(userState);
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        String marketplaceEntry = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        var subcategories = userState.getAllDataEntriesByPrefix(SUBCATEGORY);

        var expectedSubcategories = priceAggregatorService.getSubcategories(marketplaceEntry, category, subcategories);
        String subcategory = update.getMessage().getText();

        if (expectedSubcategories.contains(subcategory)) {
            return Result.of(userState.addDataEntry(getNextSubcategoryKey(subcategories), subcategory));
        }

        telegramSenderService.sendMessage(chatId, WRONG_SUBCATEGORY_MESSAGE + String.join(", ", expectedSubcategories));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        onStart(update, userState.removeDataEntriesByPrefix(SUBCATEGORY));
        return Result.of(userState.removeDataEntriesByPrefix(SUBCATEGORY));
    }

    private String getNextSubcategoryKey(Map<String, String> subcategories) {
        return SUBCATEGORY + (subcategories.size() + 1);
    }
}
