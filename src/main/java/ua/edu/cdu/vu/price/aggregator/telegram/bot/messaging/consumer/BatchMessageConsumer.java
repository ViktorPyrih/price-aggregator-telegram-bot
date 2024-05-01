package ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.consumer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.MessageTopic;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class BatchMessageConsumer<KEY, MESSAGE> implements Runnable {

    @NonNull
    private final KEY key;
    private final long timeout;
    @NonNull
    private final MessageTopic<KEY, MESSAGE> topic;
    @NonNull
    private final Consumer<List<MESSAGE>> processor;

    @Override
    public void run() {
        var messages = consume(key, timeout);
        if (!messages.isEmpty()) {
            processor.accept(messages);
        }
    }

    private List<MESSAGE> consume(KEY key, long timeout) {
        log.debug("Consuming message from topic: {} with key: {}", topic.getName(), key);
        var messages = topic.consumeBatch(key, timeout);
        log.debug("Messages: {} consumed from topic: {} with key: {}", messages, topic.getName(), key);

        return messages;
    }
}
