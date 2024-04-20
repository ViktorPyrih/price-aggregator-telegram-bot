package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

public record UserState(Long userId, int flowId, int stepId, Map<String, String> data) {

    private static final int INITIAL_ID = 1;

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

        updatedData.put(key, value);

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
