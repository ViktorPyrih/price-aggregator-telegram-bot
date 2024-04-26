package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step.Step;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.TelegramSenderService;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.util.Buttons;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.AGGREGATOR_FLOW_ID;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.BACK;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getChatId;

@Component
@RequiredArgsConstructor
public class ChooseMinPriceStep implements Step {

    private static final String CHOOSE_MIN_PRICE_MESSAGE = "Choose min price, please";

    private final TelegramSenderService telegramSenderService;

    @Override
    public int flowId() {
        return AGGREGATOR_FLOW_ID;
    }

    @Override
    public int stepId() {
        return CHOOSE_MIN_PRICE_STEP_ID;
    }

    @Override
    public Result process(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        telegramSenderService.send(chatId, CHOOSE_MIN_PRICE_MESSAGE, Buttons.keyboard(BACK));

        return Result.of(userState.nextStep());
    }

    @Override
    public Result processBack(Update update, UserState userState) throws TelegramApiException {
        long chatId = getChatId(update);
        telegramSenderService.send(chatId, CHOOSE_MIN_PRICE_MESSAGE, Buttons.keyboard(BACK));

        return Result.of(userState.nextStep());
    }
}
