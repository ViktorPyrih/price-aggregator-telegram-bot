package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;

import java.util.List;
import java.util.Optional;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;

@RequiredArgsConstructor
public abstract class FilterStep implements Step {

    final PriceAggregatorService priceAggregatorService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    List<Filter> getFilters(UserState userState) {
        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        String subcategory = userState.getDataEntry(SUBCATEGORY);
        String subcategory2 = userState.getDataEntry(SUBCATEGORY2);

        return priceAggregatorService.getFilters(marketplace, category, subcategory, subcategory2);
    }

    List<String> extractKeys(List<Filter> filters) {
        return filters.stream()
                .map(Filter::getKey)
                .toList();
    }

    Optional<List<String>> extractValues(List<Filter> filters, String key) {
        return filters.stream()
                .filter(filter -> filter.getKey().equals(key))
                .findAny()
                .map(Filter::getValues);
    }
}
