package com.readutf.matchmaker.queue;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MatchMaker {

    @NotNull List<List<UUID>> onIteration(Collection<QueueEntry> queueEntry);

}
