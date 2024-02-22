package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import lombok.Getter;

@Getter
public class QueueUpdateEvent extends QueueEvent {

    private final Queue queue;

    public QueueUpdateEvent(Queue queue) {
        this.queue = queue;
    }
}
