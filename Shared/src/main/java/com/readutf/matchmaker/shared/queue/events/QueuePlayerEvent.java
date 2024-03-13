package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter @ToString
public class QueuePlayerEvent extends QueueEvent {

    private final Queue queue;
    private final boolean joining;
    private final List<UUID> playerIds;

    public QueuePlayerEvent(Queue queue, boolean joining, List<UUID> playerIds) {
        this.queue = queue;
        this.joining = joining;
        this.playerIds = playerIds;
    }
}
