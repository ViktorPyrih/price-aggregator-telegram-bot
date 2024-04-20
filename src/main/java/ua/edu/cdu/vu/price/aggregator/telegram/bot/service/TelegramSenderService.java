package ua.edu.cdu.vu.event.notification.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.event.notification.telegram.bot.EventNotificationTelegramBotApplication.EventNotificationTelegramBot;

@Service
@RequiredArgsConstructor
public class TelegramSenderService {

    private final ObjectProvider<EventNotificationTelegramBot> botObjectProvider;

    public void send(long chatId, String message, boolean removeReplyKeyboard) throws TelegramApiException {
        botObjectProvider.getObject().execute(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode(ParseMode.HTML)
                .replyMarkup(removeReplyKeyboard
                        ? ReplyKeyboardRemove.builder().removeKeyboard(true).build()
                        : null)
                .build());
    }

    public void send(long chatId, String message) throws TelegramApiException {
        send(chatId, message, true);
    }

    public void send(long chatId, String text, ReplyKeyboardMarkup markup) throws TelegramApiException {
        botObjectProvider.getObject().execute(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .replyMarkup(markup)
                .build());
    }

    public void send(long chatId, String message, InlineKeyboardMarkup markup) throws TelegramApiException {
        botObjectProvider.getObject().execute(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode(ParseMode.HTML)
                .replyMarkup(markup)
                .build());
    }
}
