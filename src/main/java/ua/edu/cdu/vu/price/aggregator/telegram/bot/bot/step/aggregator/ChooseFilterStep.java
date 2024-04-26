package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import java.util.HashSet;
import java.util.Set;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.FILTER_KEY_PREFIX;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
public class ChooseFilterStep extends FilterStep {

    private static final String FILTER_SELECTED_TEMPLATE = "Filter selected: '%s: %s'";
    private static final String WRONG_FILTER_VALUE_MESSAGE = "Please, choose one of the following filter values: ";

    private final TelegramSenderService telegramSenderService;
    private final ObjectMapper objectMapper;

    public ChooseFilterStep(PriceAggregatorService priceAggregatorService, TelegramSenderService telegramSenderService, ObjectMapper objectMapper) {
        super(priceAggregatorService);
        this.telegramSenderService = telegramSenderService;
        this.objectMapper = objectMapper;
    }

    @Override
    public int stepId() {
        return CHOOSE_FILTER_STEP_ID;
    }

    @Override
    @SneakyThrows
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);

        var filters = getFilters(userState);
        String filterKey = userState.getDataEntry(FILTER_KEY);
        String filterValue = update.getMessage().getText();
        var filterValues = extractValues(filters, filterKey).orElseThrow();

        if (filterValues.contains(filterValue)) {
            Set<String> filterValuesToStore = new HashSet<>();
            filterValuesToStore.add(filterValue);

            if (userState.hasDataEntry(FILTER_KEY_PREFIX + filterKey)) {
                Set<String> storedFilterValues = objectMapper.readValue(userState.getDataEntry(FILTER_KEY_PREFIX + filterKey), new TypeReference<>() {});
                filterValuesToStore.addAll(storedFilterValues);
            }

            telegramSenderService.send(chatId, FILTER_SELECTED_TEMPLATE.formatted(filterKey, filterValuesToStore));

            return Result.of(userState.addDataEntry(FILTER_KEY_PREFIX + filterKey, objectMapper.writeValueAsString(filterValuesToStore)));
        }

        telegramSenderService.send(chatId, WRONG_FILTER_VALUE_MESSAGE + String.join(", ", filterValues));

        return Result.of(userState);
    }

    @Override
    public Result processBack(Update update, UserState userState) {
        return Result.of(CHOOSE_FILTER_KEY_STEP_ID, userState);
    }
}
