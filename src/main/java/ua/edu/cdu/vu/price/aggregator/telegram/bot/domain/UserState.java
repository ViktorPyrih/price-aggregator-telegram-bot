package ua.edu.cdu.vu.event.notification.telegram.bot.domain;

import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;
import static ua.edu.cdu.vu.event.notification.telegram.bot.util.TelegramBotConstants.NO_DATA;

@Value
public class UserState {

    private static final int INITIAL_ID = 1;

    Long userId;

    int flowId;
    int stepId;

    Map<String, String> data;

    public static UserState initial(long userId, int flowId) {
        return new UserState(userId, flowId, INITIAL_ID, new HashMap<>());
    }

    public UserState nextStep() {
        return new UserState(userId, flowId, stepId + 1, data);
    }

    public UserState firstStep() {
        return new UserState(userId, flowId, INITIAL_ID, data);
    }

    public UserState addDataEntry(String key, String value) {
        var updatedData = data;
        if (isNull(updatedData)) {
            updatedData = new HashMap<>();
        }
        if (!NO_DATA.equals(value)) {
            updatedData.put(key, value);
        }

        return new UserState(userId, flowId, stepId, updatedData);
    }

    public UserState addDataEntry(String key, long value) {
        return addDataEntry(key, String.valueOf(value));
    }

    public String getDataEntry(String key) {
        return findDataEntry(key).orElse(null);
    }

    public Optional<String> findDataEntry(String key) {
        if (isNull(data)) {
            return Optional.empty();
        }

        return Optional.ofNullable(data.get(key));
    }

    public void removeDataEntry(String key) {
        if (isNull(data)) {
            return;
        }

        data.remove(key);
    }
}
