package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import java.util.List;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;

@RequiredArgsConstructor
public abstract class FilterStep implements Step {

    final PriceAggregatorService priceAggregatorService;
    final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    List<Filter> getFilters(UserState userState) {
        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        var subcategories = userState.getAllDataEntriesByPrefix(SUBCATEGORY);

        return priceAggregatorService.getFilters(marketplace, category, subcategories);
    }

    @Override
    public Result processComplete(Update update, UserState userState) {
        return Result.of(CHOOSE_MIN_PRICE_STEP_ID, userState);
    }
}
