package ua.edu.cdu.vu.price.aggregator.telegram.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.edu.cdu.vu.price.aggregator.telegram.bot.repository.UserTaskRepository;

import java.util.Optional;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserTaskService {

    private final UserTaskRepository userTaskRepository;

    public void save(long userId, Future<?> task) {
        userTaskRepository.save(userId, task);
    }

    public Optional<Future<?>> findTask(long userId) {
        return userTaskRepository.findTask(userId);
    }
}
