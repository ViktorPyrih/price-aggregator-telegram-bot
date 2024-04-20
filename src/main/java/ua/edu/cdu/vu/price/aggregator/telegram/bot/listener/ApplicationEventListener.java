package ua.edu.cdu.vu.price.aggregator.telegram.bot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.PriceAggregatorTelegramBot;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final TelegramBotsApi api;
    private final PriceAggregatorTelegramBot bot;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() throws TelegramApiException {
        log.info("Registering bot: {}", bot.getBotUsername());
        api.registerBot(bot);
        log.info("Bot registered");
    }
}
