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
    private final int maxTeamSize;
    private final int minTeamSize;
    private final int numberOfTeams;

    public Queue(String name, String matchMakerId, String serverFilterId, int maxTeamSize, int minTeamSize, int numberOfTeams) {
        this.queueId = UUID.randomUUID();
        this.name = name;
        this.matchMakerId = matchMakerId;
        this.serverFilterId = serverFilterId;
        this.inQueue = new ArrayDeque<>();
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSize;
        this.numberOfTeams = numberOfTeams;
    }
}
