package ua.edu.cdu.vu.price.aggregator.telegram.bot.messaging;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class InMemoryTopic<KEY, MESSAGE> implements MessageTopic<KEY, MESSAGE> {

    @NonNull
    private final String name;
    private final Map<KEY, BlockingQueue<MESSAGE>> partitions = new ConcurrentHashMap<>();

    @Override
    public void produce(@NonNull KEY key, @NonNull MESSAGE message) {
        partitions.merge(key, new LinkedBlockingQueue<>(List.of(message)), (oldQueue, newQueue) -> {
            oldQueue.addAll(newQueue);
            return oldQueue;
        });
    }

    @Override
    public Optional<MESSAGE> consume(@NonNull KEY key, long timeout) {
        return Optional.ofNullable(partitions.get(key))
                .map(partition -> poll(partition, timeout));
    }

    private MESSAGE poll(BlockingQueue<MESSAGE> queue, long timeout) {
        try {
            return queue.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
