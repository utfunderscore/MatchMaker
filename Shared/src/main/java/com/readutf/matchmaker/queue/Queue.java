package com.readutf.matchmaker.queue;

import lombok.Getter;

import java.util.*;
import java.util.function.Predicate;

@Getter
public class Queue {

    private final String name, matchMakerId, serverFilterId;
    private final List<QueueEntry> inQueue;
    private final int maxTeamSize;
    private final int minTeamSize;
    private final int numberOfTeams;

    public Queue(String name, String matchMakerId, String serverFilterId, int maxTeamSize, int minTeamSize, int numberOfTeams) {
        this.name = name;
        this.matchMakerId = matchMakerId;
        this.serverFilterId = serverFilterId;
        this.inQueue = new ArrayList<>();
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSize;
        this.numberOfTeams = numberOfTeams;
    }

    public void addToQueue(UUID playerId) {
        if(inQueue.stream().anyMatch(queueEntry -> queueEntry.getPlayers().contains(playerId))) throw new IllegalArgumentException("Player already in queue");
        inQueue.add(QueueEntry.create(playerId));
    }

    public int getQueueSize() {
        return inQueue.stream().mapToInt(queueEntry -> queueEntry.getPlayers().size()).sum();
    }

}
