package com.readutf.matchmaker.queue.impl;

import com.readutf.matchmaker.queue.MatchMaker;
import com.readutf.matchmaker.queue.QueueEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BasicMatchMaker implements MatchMaker {
    @Override
    public @NotNull List<List<UUID>> onIteration(Collection<QueueEntry> queueEntry) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
