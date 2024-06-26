package ua.edu.cdu.vu.price.aggregator.telegram.bot.domain;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record UserState(long userId, int flowId, int stepId, Map<String, String> data) {

    private static final int INITIAL_ID = 1;

    public static UserState initial(long userId, int flowId) {
        return new UserState(userId, flowId, INITIAL_ID, new HashMap<>());
    }

    public UserState nextStep() {
        return new UserState(userId, flowId, stepId + 1, data);
    }

    public UserState previousStep() {
        if (stepId == INITIAL_ID) {
            return this;
        }

        return new UserState(userId, flowId, stepId - 1, data);
    }

    public UserState withStepId(int stepId) {
        return new UserState(userId, flowId, stepId, data);
    }

    public UserState addDataEntry(String key, String value) {
        var updatedData = data;
        if (isNull(updatedData)) {
            updatedData = new HashMap<>();
        }

        updatedData.put(key, value);

        return new UserState(userId, flowId, stepId, updatedData);
    }

    public UserState addDataEntry(String key, int value) {
        return addDataEntry(key, String.valueOf(value));
    }

    public String getDataEntry(String key) {
        return findDataEntry(key).orElse(null);
    }

    public Map<String, String> getAllDataEntriesByPrefix(String prefix) {
        if (isNull(data)) {
            return Collections.emptyMap();
        }

        return data.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<String> findDataEntry(String key) {
        if (isNull(data)) {
            return Optional.empty();
        }

        return Optional.ofNullable(data.get(key));
    }

    public boolean hasDataEntry(String key) {
        return findDataEntry(key).isPresent();
    }

    public UserState removeDataEntriesByPrefix(String prefix) {
        if (nonNull(data)) {
            data.keySet().removeIf(key -> key.startsWith(prefix));
        }

        return this;
    }

    public UserState removeDataEntry(String key) {
        if (nonNull(data)) {
            data.remove(key);
        }

        return this;
    }

    public UserState initialStep() {
        return initial(userId, flowId);
    }
}
