package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot.step;

import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.domain.UserState;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;

public interface Step {

    // search
    int ENTER_QUERY_STEP_ID = 1;

    // aggregator
    int CHOOSE_MARKETPLACE_STEP_ID = 1;
    int CHOOSE_CATEGORY_STEP_ID = 2;
    int CHOOSE_SUBCATEGORY_STEP_ID = 3;
    int CHOOSE_FILTER_KEY_STEP_ID = 4;
    int CHOOSE_FILTER_VALUE_STEP_ID = 5;
    int CHOOSE_MIN_PRICE_STEP_ID = 6;
    int CHOOSE_MAX_PRICE_STEP_ID = 7;
    int SHOW_PRODUCTS_STEP_ID = 8;

    @Value(staticConstructor = "of")
    class Result {

        Integer nextStepId;
        UserState userState;

        public boolean isUserStatePresent() {
            return userState != null;
        }

        public boolean isUserStateAndNextStepIdPresent() {
            return userState != null && nextStepId != null;
        }

        public static Result of(UserState userState) {
            return new Result(null, userState);
        }
    }

    int flowId();

    int stepId();

    default Result processUpdate(Update update, UserState userState) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (BACK.equals(text)) {
            return processBack(update, userState);
        }
        if (COMPLETE.equals(text)) {
            return processComplete(update, userState);
        }
        if (RESET.equals(text)) {
            return processReset(update, userState);
        }

        return process(update, userState);
    }

    Result onStart(Update update, UserState userState) throws TelegramApiException;

    Result process(Update update, UserState userState) throws TelegramApiException;

    Result processBack(Update update, UserState userState) throws TelegramApiException;

    default Result processComplete(Update update, UserState userState) throws TelegramApiException {
        return onStart(update, userState);
    }

    default Result processReset(Update update, UserState userState) throws TelegramApiException {
        return process(update, userState);
    }
}
