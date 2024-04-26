package ua.edu.cdu.vu.price.aggregator.telegram.bot.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.stream.Collectors;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.BACK;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.COMPLETE;

@UtilityClass
public class Buttons {

    public static KeyboardButton button(String text) {
        return KeyboardButton.builder()
                .text(text)
                .build();
    }

    public static ReplyKeyboardMarkup keyboard(String... buttons) {
        return keyboard(List.of(buttons));
    }

    public static ReplyKeyboardMarkup keyboard(List<String> buttons) {
        return keyboard(buttons, false);
    }

    public static ReplyKeyboardMarkup keyboard(List<String> buttons, boolean addBackButton) {
        return keyboard(buttons, addBackButton, false);
    }

    public static ReplyKeyboardMarkup keyboard(List<String> buttons, boolean addBackButton, boolean addCompleteButton) {
        return ReplyKeyboardMarkup.builder()
                .isPersistent(true)
                .resizeKeyboard(true)
                .keyboard(rows(buttons, addBackButton, addCompleteButton))
                .build();
    }

    private static List<KeyboardRow> rows(List<String> buttons, boolean addBackButton, boolean addCompleteButton) {
        var rows = buttons.stream()
                .map(Buttons::row)
                .collect(Collectors.toList());
        if (addCompleteButton) {
            rows.add(row(COMPLETE));
        }
        if (addBackButton) {
            rows.add(row(BACK));
        }

        return rows;
    }

    private static KeyboardRow row(String button) {
        return new KeyboardRow(List.of(button(button)));
    }
}
