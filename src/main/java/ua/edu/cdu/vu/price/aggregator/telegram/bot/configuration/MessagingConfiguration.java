package ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.InMemoryTopic;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.MessageTopic;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.producer.MessageProducer;

@Configuration
public class MessagingConfiguration {

    @Bean
    public MessageTopic<Long, Update> updateTopic() {
        return new InMemoryTopic<>("update-topic");
    }

    @Bean
    public MessageProducer<Long, Update> updateMessageProducer(MessageTopic<Long, Update> updateTopic) {
        return new MessageProducer<>(updateTopic);
    }
}
