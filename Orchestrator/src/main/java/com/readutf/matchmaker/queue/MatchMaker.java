package com.readutf.matchmaker.queue;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MatchMaker {

    @NotNull List<List<UUID>> onIteration(Queue queue, List<QueueEntry> queueEntry) throws Exception;

    boolean validateEntry(Queue queue, QueueEntry queueEntry);

}
