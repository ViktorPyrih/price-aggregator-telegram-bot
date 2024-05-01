package ua.edu.cdu.vu.price.aggregator.telegram.bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.configuration.PriceAggregatorTelegramBotConfiguration;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.consumer.MessageConsumerFactory;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging.producer.MessageProducer;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.service.UserTaskService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.CommonConstants.BACK;
import static ua.edu.cdu.vu.price.aggregator.telegram.bot.util.TelegramUtils.getUserId;

@Slf4j
@Component
public class PriceAggregatorTelegramBot extends TelegramLongPollingBot {

    private final PriceAggregatorTelegramBotConfiguration configuration;
    private final MessageProducer<Long, Update> updateMessageProducer;
    private final MessageConsumerFactory messageConsumerFactory;
    private final ExecutorService taskExecutor;
    private final UserTaskService userTaskService;

    public PriceAggregatorTelegramBot(PriceAggregatorTelegramBotConfiguration configuration, MessageProducer<Long, Update> updateMessageProducer, MessageConsumerFactory messageConsumerFactory, ExecutorService taskExecutor, UserTaskService userTaskService) {
        super(configuration.getToken());
        this.configuration = configuration;
        this.updateMessageProducer = updateMessageProducer;
        this.messageConsumerFactory = messageConsumerFactory;
        this.taskExecutor = taskExecutor;
        this.userTaskService = userTaskService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        long userId = getUserId(update);
        log.info("Update received: {} from user: {}", update, userId);

        updateMessageProducer.produce(userId, update);

        var task = userTaskService.findTask(userId);

        boolean shouldCancelTask = task.isPresent() && shouldCancelTask(task.get(), update);
        if (shouldCancelTask) {
            task.get().cancel(true);
        }

        if (shouldCancelTask || task.isEmpty() || task.get().isDone()) {
            Future<?> newTask = taskExecutor.submit(messageConsumerFactory.createUpdateConsumer(userId));
            userTaskService.save(userId, newTask);
        }
    }

    @Override
    public String getBotUsername() {
        return configuration.getUsername();
    }

    private boolean shouldCancelTask(Future<?> task, Update update) {
        return !task.isDone() && update.hasMessage() && BACK.equals(update.getMessage().getText());
    }
}
