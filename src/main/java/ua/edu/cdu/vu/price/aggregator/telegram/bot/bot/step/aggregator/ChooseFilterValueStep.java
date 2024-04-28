package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY_PREFIX;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class ChooseFilterValueStep extends FilterStep {

    private static final String FILTER_SELECTED_TEMPLATE = "Filter selected: '%s: %s'";

    private static final String CHOOSE_FILTER_VALUES_MESSAGE = "Choose filter values, please";
    private static final String WRONG_FILTER_VALUE_MESSAGE = "Please, choose one of the following filter values: ";
    private static final String RESET_FILTERS_TEMPLATE = "Resetting filter values by key: '%s'...";

    private final ObjectMapper objectMapper;

    public ChooseFilterValueStep(PriceAggregatorService priceAggregatorService, TelegramSenderService telegramSenderService, ObjectMapper objectMapper) {
        super(priceAggregatorService, telegramSenderService);
        this.objectMapper = objectMapper;
    }

    @Override
    public int stepId() {
        return CHOOSE_FILTER_VALUE_STEP_ID;
    }

    @Override
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        String filterKey = userState.getDataEntry(FILTER_KEY);
        var filterValues = extractValues(filters, filterKey);

        telegramSenderService.send(chatId, CHOOSE_FILTER_VALUES_MESSAGE, Buttons.keyboard(filterValues, true, true, true));

        return Result.of(userState);
    }

    @Override
    @SneakyThrows
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        String filterKey = userState.getDataEntry(FILTER_KEY);

        var filterValues = extractValues(filters, filterKey);
        String filterValue = update.getMessage().getText();

        if (filterValues.contains(filterValue)) {
            Set<String> filterValuesToStore = new HashSet<>();
            filterValuesToStore.add(filterValue);

            String userStateFilterKey = FILTER_KEY_PREFIX + filterKey;
            if (userState.hasDataEntry(userStateFilterKey)) {
                Set<String> storedFilterValues = objectMapper.readValue(userState.getDataEntry(userStateFilterKey), new TypeReference<>() {
                });
                filterValuesToStore.addAll(storedFilterValues);
            }

            telegramSenderService.send(chatId, FILTER_SELECTED_TEMPLATE.formatted(filterKey, filterValuesToStore));

            return Result.of(userState.addDataEntry(userStateFilterKey, objectMapper.writeValueAsString(filterValuesToStore)));
        }

        telegramSenderService.send(chatId, WRONG_FILTER_VALUE_MESSAGE + String.join(", ", filterValues));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) {
        return Result.of(CHOOSE_FILTER_KEY_STEP_ID, userState);
    }

    @Override
    public Result processReset(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        String filterKey = userState.getDataEntry(FILTER_KEY);
        telegramSenderService.send(chatId, RESET_FILTERS_TEMPLATE.formatted(filterKey));

        return Result.of(userState.removeDataEntry(FILTER_KEY_PREFIX + filterKey));
    }

    private List<String> extractValues(List<Filter> filters, String key) {
        return filters.stream()
                .filter(filter -> filter.getKey().equals(key))
                .findAny()
                .map(Filter::getValues)
                .orElseThrow();
    }
}
