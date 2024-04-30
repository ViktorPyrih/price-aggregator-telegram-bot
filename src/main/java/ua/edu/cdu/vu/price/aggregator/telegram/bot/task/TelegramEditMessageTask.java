package ua.edu.cdu.vu.price.aggregator.telegram.bot.task;

import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

public class TelegramEditMessageTask implements Runnable {

    private static final String MESSAGE_TEMPLATE = "Searching for products... %ds";

    private final int messageId;
    private final long chatId;
    private final int frequency;

    private final TelegramSenderService telegramSenderService;

    public TelegramEditMessageTask(int messageId, long chatId, int frequency, TelegramSenderService telegramSenderService) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.frequency = frequency;
        this.telegramSenderService = telegramSenderService;
        this.elapsedSeconds = frequency;
    }

    private int elapsedSeconds;

    @Override
    public void run() {
        telegramSenderService.editUnchecked(chatId, messageId, String.format(MESSAGE_TEMPLATE, elapsedSeconds));
        elapsedSeconds += frequency;
    }
}
