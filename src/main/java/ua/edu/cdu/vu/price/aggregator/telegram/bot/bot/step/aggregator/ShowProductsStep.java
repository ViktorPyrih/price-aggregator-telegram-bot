package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.ImageUtils.decode;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.NumberUtils.tryParseToInt;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ShowProductsStep implements Step {

    private static final int FIRST_PAGE = 1;
    private static final String SEARCHING_FOR_PRODUCTS_MESSAGE = "Searching for products...";
    private static final String WRONG_PAGE_NUMBER_TEMPLATE = "Wrong page number. Please, enter a valid number in range: [1;%d]";

    private static final String IMAGE_PNG = ".image.png";
    private static final String DESCRIPTION_PNG = ".description.png";
    private static final String PRICE_PNG = ".price.png";

    private final TelegramSenderService telegramSenderService;
    private final PriceAggregatorService priceAggregatorService;
    private final FilterMapper filterMapper;
    private final ObjectMapper objectMapper;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return SHOW_PRODUCTS_STEP_ID;
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
            telegramSenderService.send(chatId, WRONG_PAGE_NUMBER_TEMPLATE.formatted(pagesCount));
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
        telegramSenderService.send(chatId, SEARCHING_FOR_PRODUCTS_MESSAGE, true);

        var products = getProducts(userState, page);

        sendProducts(chatId, products);

        return Result.of(userState.addDataEntry(PAGES_COUNT, products.pagesCount()));
    }

    private void sendProducts(long chatId, Pageable<Product> products) throws TelegramApiException {
        var pages = pages(products.pagesCount());
        for (var product : products.content()) {
            sendProduct(chatId, product, pages);
        }
    }

    private void sendProduct(long chatId, Product product, List<String> pages) throws TelegramApiException {
        telegramSenderService.send(chatId, product.getLink(), Buttons.keyboard(pages, true));

        String image = product.getLink() + IMAGE_PNG;
        String description = product.getLink() + DESCRIPTION_PNG;
        String price = product.getLink() + PRICE_PNG;
        var images = new LinkedHashMap<String, byte[]>() {{
            put(image, decode(product.getImage()));
            put(description, decode(product.getDescription()));
            put(price, decode(product.getPrice()));
        }};

        telegramSenderService.send(chatId, product.getLink(), images);
    }

    private List<Filter> extractFilters(UserState userState, String subcategory2) {
        return filterMapper.convertToDomain(userState.getAllDataEntriesByPrefix(FILTER_KEY_PREFIX).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), parseFilters(entry.getValue())))
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream()
                        .filter(value -> !value.equals(subcategory2))
                        .toList()))
                .filter(not(entry -> entry.getValue().isEmpty()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @SneakyThrows
    private List<String> parseFilters(String filters) {
        return objectMapper.readValue(filters, new TypeReference<>() {
        });
    }

    private Pageable<Product> getProducts(UserState userState, int page) {
        String marketplace = userState.getDataEntry(MARKETPLACE);
        String category = userState.getDataEntry(CATEGORY);
        String subcategory = userState.getDataEntry(SUBCATEGORY);
        String subcategory2 = userState.getDataEntry(SUBCATEGORY2);
        var filters = extractFilters(userState, subcategory2);
        double minPrice = Double.parseDouble(userState.getDataEntry(MIN_PRICE));
        double maxPrice = Double.parseDouble(userState.getDataEntry(MAX_PRICE));

        return priceAggregatorService.getProducts(marketplace, category, subcategory, subcategory2, filters, minPrice, maxPrice, page);
    }

    private static List<String> pages(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(String::valueOf)
                .toList();
    }
}
