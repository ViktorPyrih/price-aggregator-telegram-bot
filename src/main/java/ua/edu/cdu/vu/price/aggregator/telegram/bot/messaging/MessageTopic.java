package ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging;

import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface MessageTopic<KEY, MESSAGE> {

    String getName();

    void produce(@NonNull KEY key, @NonNull MESSAGE message);

    Optional<MESSAGE> consume(@NonNull KEY key, long timeout);

    default List<MESSAGE> consumeBatch(KEY key, long timeout) {
        List<MESSAGE> messages = new LinkedList<>();
        Optional<MESSAGE> message;
        do {
            message = consume(key, timeout);
            message.ifPresent(messages::add);
        } while (message.isPresent());

        return messages;
    }
}
