package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Pageable;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.Product;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.task.TelegramEditMessageTask;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.ImageUtils.decode;

@Service
@RequiredArgsConstructor
public class ProductTelegramSenderService {

    private static final String SEARCHING_FOR_PRODUCTS_MESSAGE = "Searching for products...";
    private static final String NO_PRODUCTS_FOUND_MESSAGE = "No products found";

    private static final String DESCRIPTION_PNG = ".description.png";
    private static final String PRICE_PNG = ".price.png";

    private static final String SPINNER_IMAGE = "https://cdn.dribbble.com/users/29051/screenshots/2347771/spinner.mov.gif";

    private final TelegramSenderService telegramSenderService;
    private final ScheduledExecutorService taskScheduler;

    @Value("${price-aggregator-telegram-bot.scheduling.search-products-message.frequency:5}")
    private int searchProductsMessageFrequency;

    public void sendProducts(long chatId, Supplier<Pageable<Product>> proudctsSupplier) throws TelegramApiException {
        sendProducts(chatId, proudctsSupplier, false);
    }

    public int sendProducts(long chatId, Supplier<Pageable<Product>> proudctsSupplier, boolean pagination) throws TelegramApiException {
        int messageId = telegramSenderService.sendMessage(chatId, SEARCHING_FOR_PRODUCTS_MESSAGE);
        telegramSenderService.sendAnimation(chatId, SPINNER_IMAGE);

        TelegramEditMessageTask task = new TelegramEditMessageTask(messageId, chatId, searchProductsMessageFrequency, telegramSenderService);
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(task, searchProductsMessageFrequency, searchProductsMessageFrequency, TimeUnit.SECONDS);

        Pageable<Product> products;
        try {
            products = proudctsSupplier.get();
        } finally {
            future.cancel(true);
        }

        sendProducts(chatId, products, pagination);

        return products.pagesCount();
    }

    private void sendProducts(long chatId, Pageable<Product> products, boolean pagination) throws TelegramApiException {
        if (products.content().isEmpty()) {
            telegramSenderService.sendMessage(chatId, NO_PRODUCTS_FOUND_MESSAGE);
            return;
        }
        var pages = pagination ? pages(products.pagesCount()) : Collections.<String>emptyList();
        for (var product : products.content()) {
            sendProduct(chatId, product, pages);
        }
    }

    private static List<String> pages(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(String::valueOf)
                .toList();
    }

    private void sendProduct(long chatId, Product product, List<String> pages) throws TelegramApiException {
        telegramSenderService.sendMessage(chatId, product.getLink(), Buttons.keyboard(pages, true));

        String description = product.getLink() + DESCRIPTION_PNG;
        String price = product.getLink() + PRICE_PNG;
        var images = new LinkedHashMap<String, byte[]>() {{
            put(description, decode(product.getDescription()));
            put(price, decode(product.getPrice()));
        }};

        telegramSenderService.sendAlbum(chatId, product.getLink(), List.of(product.getImage()), images);
    }
}
