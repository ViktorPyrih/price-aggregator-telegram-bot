package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.*;

@UtilityClass
public class Buttons {

    public static KeyboardButton button(String text) {
        return KeyboardButton.builder()
                .text(text)
                .build();
    }

    public static InlineKeyboardButton inlineButton(String text, String data) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(data)
                .build();
    }

    public static ReplyKeyboardMarkup keyboard(String... buttons) {
        return keyboard(List.of(buttons));
    }

    public static ReplyKeyboardMarkup keyboard(Collection<String> buttons) {
        return keyboard(buttons, false);
    }

    public static ReplyKeyboardMarkup keyboard(Collection<String> buttons, boolean addBackButton) {
        return keyboard(buttons, addBackButton, false);
    }

    public static ReplyKeyboardMarkup keyboard(Collection<String> buttons, boolean addBackButton, boolean addCompleteButton) {
        return keyboard(buttons, addBackButton, addCompleteButton, false, true);
    }

    public static ReplyKeyboardMarkup keyboard(Collection<String> buttons, boolean addBackButton, boolean addCompleteButton, boolean addResetButton) {
        return keyboard(buttons, addBackButton, addCompleteButton, addResetButton, true);
    }

    public static ReplyKeyboardMarkup keyboard(Collection<String> buttons, boolean addBackButton, boolean addCompleteButton, boolean addResetButton, boolean addExitButton) {
        return ReplyKeyboardMarkup.builder()
                .isPersistent(true)
                .resizeKeyboard(true)
                .keyboard(rows(buttons, addBackButton, addCompleteButton, addResetButton, addExitButton))
                .build();
    }

    public InlineKeyboardMarkup inlineKeyboard(List<InlineKeyboardButton> buttons) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(buttons))
                .build();
    }

    private static List<KeyboardRow> rows(Collection<String> buttons, boolean addBackButton, boolean addCompleteButton, boolean addResetButton, boolean addExitButton) {
        var rows = buttons.stream()
                .map(Buttons::row)
                .collect(Collectors.toList());
        if (addCompleteButton) {
            rows.add(row(COMPLETE));
        }
        if (addBackButton) {
            rows.add(row(BACK));
        }
        if (addResetButton) {
            rows.add(row(RESET));
        }
        if (addExitButton) {
            rows.add(row(EXIT));
        }

        return rows;
    }

    private static KeyboardRow row(String button) {
        return new KeyboardRow(List.of(button(button)));
    }
}
