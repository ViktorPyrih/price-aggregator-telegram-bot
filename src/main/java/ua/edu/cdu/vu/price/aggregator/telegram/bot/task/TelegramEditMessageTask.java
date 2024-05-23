package ua.edu.cdu.vu.price.aggregator.telegram.bot.task;

import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import java.util.function.Function;

public class TelegramEditMessageTask implements Runnable {

    private final int messageId;
    private final long chatId;
    private final int frequency;
    private final Function<Integer, String> messageTemplate;

    private final TelegramSenderService telegramSenderService;

    public TelegramEditMessageTask(int messageId, long chatId, int frequency, Function<Integer, String> messageTemplate, TelegramSenderService telegramSenderService) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.frequency = frequency;
        this.messageTemplate = messageTemplate;
        this.telegramSenderService = telegramSenderService;
        this.elapsedSeconds = frequency;
    }

    private int elapsedSeconds;

    @Override
    public void run() {
        telegramSenderService.editMessageUnchecked(chatId, messageId, messageTemplate.apply(elapsedSeconds));
        elapsedSeconds += frequency;
    }
}
