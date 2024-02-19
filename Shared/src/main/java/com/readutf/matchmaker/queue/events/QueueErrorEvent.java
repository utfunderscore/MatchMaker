package com.readutf.matchmaker.queue.events;

import com.readutf.matchmaker.queue.QueueEvent;
import lombok.Getter;

@Getter
public class QueueErrorEvent extends QueueEvent {

    private final String error;

    public QueueErrorEvent(String queueId, String error) {
        super(queueId);
        this.error = error;
    }
}
