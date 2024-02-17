package com.readutf.matchmaker.queue.store;

import com.readutf.matchmaker.queue.Queue;

import java.util.Collection;
import java.util.List;

public interface QueueStore {

    void saveQueues(Collection<Queue> queues);

    List<Queue> loadQueues();

}
