package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.queue.QueueEvent;
import lombok.Getter;

@Getter
public class QueueErrorEvent extends QueueEvent {

    private final String queueId;
    private final String error;

    public QueueErrorEvent(String queueId, String error) {
        this.queueId = queueId;
        this.error = error;
    }
}
