package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Pageable;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.task.TelegramEditMessageTask;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Base64Utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.command.BotCommand.SEARCH;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Base64Utils.decode;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons.*;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CallbackDataUtils.extractSearchQuery;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.BACK;

@Service
@RequiredArgsConstructor
public class ProductTelegramSenderService {

    private static final String ALL = "ALL";
    private static final String SEARCHING_FOR_PRODUCTS_MESSAGE = "Searching for products on marketplace: '%s'";
    private static final String SEARCHING_FOR_PRODUCTS_MESSAGE_WITH_QUERY = "Searching for products on marketplace: '%s'\nQuery: '%s'";
    private static final String ELAPSED_TIME_MESSAGE = "\nElapsed time: %d seconds";
    private static final String NO_PRODUCTS_FOUND_MESSAGE = "No products found";
    private static final String TITLE_AND_PRICE_MESSAGE = "%s - %s";

    private static final String DESCRIPTION_PNG = ".description.png";
    private static final String PRICE_PNG = ".price.png";

    private static final String SEARCH_CALLBACK_DATA = SEARCH + " %s %s";

    private final TelegramSenderService telegramSenderService;
    private final ScheduledExecutorService taskScheduler;
    private final PriceAggregatorService priceAggregatorService;
    private final TableGeneratorService<List<String>> tableGeneratorService;
    private final PaginationService paginationService;

    @Value("${price-aggregator-telegram-bot.scheduling.tasks.edit-search-products-message.rate-seconds:5}")
    private int editTaskRateSeconds;

    public void sendProducts(long chatId, String query, Supplier<Pageable<Product>> proudctsSupplier) throws TelegramApiException {
        sendProducts(chatId, ALL, query, 0, proudctsSupplier, false);
    }

    public int sendProducts(long chatId, String marketplace, int page, Supplier<Pageable<Product>> proudctsSupplier, boolean pagination) throws TelegramApiException {
        return sendProducts(chatId, marketplace, null, page, proudctsSupplier, pagination);
    }

    public void sendProducts(long chatId, String marketplace, String query, Supplier<Pageable<Product>> proudctsSupplier) throws TelegramApiException {
        sendProducts(chatId, marketplace, query, 0, proudctsSupplier, false);
    }

    public int sendProducts(long chatId, String marketplace, String query, int page, Supplier<Pageable<Product>> proudctsSupplier, boolean pagination) throws TelegramApiException {
        String message = isNull(query) ? SEARCHING_FOR_PRODUCTS_MESSAGE.formatted(marketplace) : SEARCHING_FOR_PRODUCTS_MESSAGE_WITH_QUERY.formatted(marketplace, query);
        int messageId = telegramSenderService.sendMessage(chatId, message);

        Function<Integer, String> messageTemplate = (elapsedTime) -> message + ELAPSED_TIME_MESSAGE.formatted(elapsedTime);
        TelegramEditMessageTask task = new TelegramEditMessageTask(messageId, chatId, editTaskRateSeconds, messageTemplate, telegramSenderService);
        var future = taskScheduler.scheduleAtFixedRate(task, editTaskRateSeconds, editTaskRateSeconds, TimeUnit.SECONDS);

        Pageable<Product> products;
        try {
            products = proudctsSupplier.get();
        } finally {
            future.cancel(true);
        }

        sendProducts(chatId, products, page, pagination);

        List<String> summarizedTables = tableGeneratorService.generateProductPriceTable(products.content());
        summarizedTables.forEach(table -> telegramSenderService.sendMessageUnchecked(chatId, table));

        return products.pagesCount();
    }

    private void sendProducts(long chatId, Pageable<Product> products, int page, boolean pagination) throws TelegramApiException {
        if (products.content().isEmpty()) {
            telegramSenderService.sendMessage(chatId, NO_PRODUCTS_FOUND_MESSAGE, keyboard(BACK));
            return;
        }
        var pages = pagination ? paginationService.pages(page, products.pagesCount()) : Collections.<String>emptyList();
        var marketplaces = priceAggregatorService.getMarketplaces().keySet();
        for (var product : products.content()) {
            sendProduct(chatId, product, pages, marketplaces);
        }
    }

    private void sendProduct(long chatId, Product product, List<String> pages, Collection<String> marketplaces) throws TelegramApiException {
        String query = Base64Utils.encode(extractSearchQuery(product.getTitle()));
        String productAndPrice = TITLE_AND_PRICE_MESSAGE.formatted(product.getTitle(), product.getPrice());
        telegramSenderService.sendMessage(chatId, productAndPrice, inlineKeyboard(buttons(marketplaces, query)));
        telegramSenderService.sendMessage(chatId, product.getLink(), keyboard(pages, true));

        String description = Base64Utils.encode(product.getLink()) + DESCRIPTION_PNG;
        String price = Base64Utils.encode(product.getLink()) + PRICE_PNG;
        var images = new LinkedHashMap<String, byte[]>();
        images.put(description, decode(product.getDescriptionImage()));
        images.put(price, decode(product.getPriceImage()));

        telegramSenderService.sendAlbum(chatId, product.getLink(), List.of(product.getImage()), images);
    }

    private List<InlineKeyboardButton> buttons(Collection<String> marketplaces, String query) {
        return marketplaces.stream()
                .map(marketplace -> {
                    String data = SEARCH_CALLBACK_DATA.formatted(marketplace, query);
                    return inlineButton(marketplace, data);
                })
                .toList();
    }
}
