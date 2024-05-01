package ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.producer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.MessageTopic;

@Slf4j
@RequiredArgsConstructor
public class MessageProducer<KEY, MESSAGE> {

    @NonNull
    private final MessageTopic<KEY, MESSAGE> topic;

    public void produce(KEY key, MESSAGE message) {
        log.debug("Producing message: {} to topic: {} with key: {}", message, topic.getName(), key);
        topic.produce(key, message);
        log.debug("Message: {} produced to topic: {} with key: {}", message, topic.getName(), key);
    }
}
