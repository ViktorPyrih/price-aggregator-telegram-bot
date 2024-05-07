package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Filter;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Pageable;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.mapper.FilterMapper;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.PriceAggregatorService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.ProductTelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.NumberUtils.tryParseToInt;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ShowProductsStep implements Step {

    private static final int FIRST_PAGE = 1;
    private static final String WRONG_PAGE_NUMBER_TEMPLATE = "Wrong page number. Please, enter a valid number in range: [1;%d]";

    private final TelegramSenderService telegramSenderService;
    private final PriceAggregatorService priceAggregatorService;
    private final FilterMapper filterMapper;
    private final ObjectMapper objectMapper;
    private final ProductTelegramSenderService productTelegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return SHOW_PRODUCTS_STEP_ID;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public Result onStart(Update update, UserState userState) throws TelegramApiException {
        return process(update, userState, FIRST_PAGE);
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        int pagesCount = userState.findDataEntry(PAGES_COUNT)
                .map(Integer::parseInt)
                .orElse(FIRST_PAGE);
        Optional<Integer> page = tryParseToInt(update.getMessage().getText());

        if (page.isEmpty() || page.get() <= 0 || page.get() > pagesCount) {
            telegramSenderService.sendMessage(chatId, WRONG_PAGE_NUMBER_TEMPLATE.formatted(pagesCount));
            return Result.of(userState);
        }

        return process(update, userState, page.get());
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        throw new UnsupportedOperationException();
    }

    private Result process(Update update, UserState userState, int page) throws TelegramApiException {
        long chatId = getChatId(update);
        String marketplace = userState.getDataEntry(MARKETPLACE);

        int pagesCount = productTelegramSenderService.sendProducts(chatId, marketplace, () -> getProducts(userState, page), true);

        return Result.of(userState.addDataEntry(PAGES_COUNT, pagesCount));
    }

    @SneakyThrows
    private List<String> parseFilters(String filters) {
        return objectMapper.readValue(filters, new TypeReference<>() {
        });
    }

    private Pageable<Product> getProducts(UserState userState, int page) {
        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        var subcategories = userState.getAllDataEntriesByPrefix(SUBCATEGORY);
        var filters = extractFilters(userState);
        double minPrice = Double.parseDouble(userState.getDataEntry(MIN_PRICE));
        double maxPrice = Double.parseDouble(userState.getDataEntry(MAX_PRICE));

        return priceAggregatorService.getProducts(marketplace, category, subcategories, filters, minPrice, maxPrice, page);
    }

    private List<Filter> extractFilters(UserState userState) {
        return filterMapper.convertToDomain(userState.getAllDataEntriesByPrefix(FILTER_KEY_PREFIX).entrySet().stream()
                .map(entry -> Map.entry(extractFilterKey(entry.getKey()), parseFilters(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private String extractFilterKey(String key) {
        return StringUtils.substringAfter(key, FILTER_KEY_PREFIX);
    }
}
