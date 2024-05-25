package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.processor;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@FunctionalInterface
public interface TelegramApiRunnable {

    void run() throws TelegramApiException;
}
