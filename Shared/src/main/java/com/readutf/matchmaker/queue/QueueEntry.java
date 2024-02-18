package com.readutf.matchmaker.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter @AllArgsConstructor
public class QueueEntry {

    private final UUID entryId;
    private final long enqueueTime;
    private final List<UUID> players;

    public static QueueEntry create(UUID playerId) {
        return new QueueEntry(playerId, System.currentTimeMillis(), List.of(playerId));
    }

}
