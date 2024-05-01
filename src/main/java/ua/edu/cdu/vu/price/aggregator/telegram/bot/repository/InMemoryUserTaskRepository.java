package ua.edu.cdu.vu.price.aggregator.telegram.bot.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Repository
public class InMemoryUserTaskRepository implements UserTaskRepository {

    private final Map<Long, Future<?>> tasks = new ConcurrentHashMap<>();

    @Override
    public void save(long userId, Future<?> task) {
        tasks.put(userId, task);
    }

    @Override
    public Optional<Future<?>> findTask(long userId) {
        return Optional.ofNullable(tasks.get(userId));
    }
}
