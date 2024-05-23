package ua.edu.cdu.vu.price.aggregator.telegram.bot.task;

import lombok.RequiredArgsConstructor;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.SPINNER_IMAGE;

@RequiredArgsConstructor
public class TelegramSpinnerTask implements Runnable {

    private final long chatId;
    private final TelegramSenderService telegramSenderService;

    @Override
    public void run() {
        telegramSenderService.sendAnimationUnchecked(chatId, SPINNER_IMAGE);
    }
}
