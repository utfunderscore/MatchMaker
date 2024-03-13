package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.queue.QueueEvent;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class QueueDeleteEvent extends QueueEvent {

    private final String queueName;

    public QueueDeleteEvent(String queueName) {
        this.queueName = queueName;
    }
}
