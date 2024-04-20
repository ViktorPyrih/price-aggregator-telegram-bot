package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration.PriceAggregatorTelegramBotConfiguration;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramBotService;

@Slf4j
@Component
public class PriceAggregatorTelegramBot extends TelegramLongPollingBot {

    private final PriceAggregatorTelegramBotConfiguration configuration;
    private final TelegramBotService telegramBotService;

    public PriceAggregatorTelegramBot(PriceAggregatorTelegramBotConfiguration configuration, TelegramBotService telegramBotService) {
        super(configuration.getToken());
        this.configuration = configuration;
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Update received: {}", update);
        telegramBotService.process(update);
    }

    @Override
    public String getBotUsername() {
        return configuration.getUsername();
    }
}
