package com.readutf.matchmaker.queue.events;

import com.readutf.matchmaker.queue.QueueEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class QueuePlayerEvent extends QueueEvent {

    private final String queueId;
    private final boolean joining;
    private final UUID playerId;

    public QueuePlayerEvent(String queueId, boolean joining, UUID playerId) {
        super(queueId);
        this.queueId = queueId;
        this.joining = joining;
        this.playerId = playerId;
    }
}
