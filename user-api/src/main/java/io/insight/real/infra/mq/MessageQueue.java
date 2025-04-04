package io.insight.real.infra.mq;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class MessageQueue<T> {
    private final Queue<T> queue = new ConcurrentLinkedQueue<>();

    public void addMessage(T data) {
        queue.add(data);
    }

    public List<T> getBatch() {
        List<T> batch = List.copyOf(queue);
        queue.removeAll(batch);
        return batch;
    }

    public int size() {
        return queue.size();
    }
}
