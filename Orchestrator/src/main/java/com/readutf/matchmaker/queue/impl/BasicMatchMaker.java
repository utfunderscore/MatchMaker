package com.readutf.matchmaker.queue.impl;

import com.readutf.matchmaker.queue.MatchMaker;
import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.QueueEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicMatchMaker implements MatchMaker {

    @Override
    public @NotNull List<List<UUID>> onIteration(Queue queue, List<QueueEntry> queueEntry) {
        List<List<UUID>> teams = new ArrayList<>();

        for (QueueEntry entry : queueEntry) {
            teams.add(entry.getPlayers());
        }

        return teams;
    }

    @Override
    public boolean validateEntry(Queue queue, QueueEntry queueEntry) {
        return queueEntry.getPlayers().size() == queue.getMaxTeamSize();
    }
}
