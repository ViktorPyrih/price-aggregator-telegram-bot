package ua.edu.cdu.vu.price.aggregator.telegram.bot.repository;

import java.util.Optional;
import java.util.concurrent.Future;

public interface UserTaskRepository {

    void save(long userId, Future<?> task);

    Optional<Future<?>> findTask(long userId);
}
