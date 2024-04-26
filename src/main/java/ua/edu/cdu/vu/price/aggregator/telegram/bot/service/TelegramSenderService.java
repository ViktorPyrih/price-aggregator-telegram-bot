package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.PriceAggregatorTelegramBot;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramSenderService {

    private final ObjectProvider<PriceAggregatorTelegramBot> botProvider;

    public void send(long chatId, String message, boolean removeReplyKeyboard) throws TelegramApiException {
        botProvider.getObject().execute(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode(ParseMode.HTML)
                .replyMarkup(removeReplyKeyboard
                        ? ReplyKeyboardRemove.builder().removeKeyboard(true).build()
                        : null)
                .build());
    }

    public void send(long chatId, String message) throws TelegramApiException {
        send(chatId, message, false);
    }

    public void send(long chatId, String text, ReplyKeyboardMarkup markup) throws TelegramApiException {
        botProvider.getObject().execute(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(markup)
                .build());
    }

    public void sendUnchecked(long chatId, String message) {
        try {
            send(chatId, message);
        } catch (TelegramApiException e) {
            log.error("Failed to send a message", e);
        }
    }
}
