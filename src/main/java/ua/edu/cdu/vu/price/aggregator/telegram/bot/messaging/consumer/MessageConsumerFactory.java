package ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.MessageTopic;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramBotService;

@Component
@RequiredArgsConstructor
public class MessageConsumerFactory {

    private final MessageTopic<Long, Update> updateTopic;
    private final TelegramBotService telegramBotService;

    @Value("${price-aggregator-telegram-bot.message-buffering.timeout-ms:3000}")
    private long bufferingTimeout;

    public BatchMessageConsumer<Long, Update> createUpdateConsumer(long userId) {
        return new BatchMessageConsumer<>(userId, bufferingTimeout, updateTopic, telegramBotService);
    }
}
