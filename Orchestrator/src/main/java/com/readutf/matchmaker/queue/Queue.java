package com.readutf.matchmaker.queue;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

@Getter
public class Queue {

    private final UUID queueId;
    private final String name, matchMakerId, serverFilterId;
    private final Collection<QueueEntry> inQueue;

    public Queue(String name, String matchMakerId, String serverFilterId) {
        this.queueId = UUID.randomUUID();
        this.name = name;
        this.matchMakerId = matchMakerId;
        this.serverFilterId = serverFilterId;
        this.inQueue = new ArrayDeque<>();
    }
}
