package com.readutf.matchmaker.shared.queue;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Queue {

    private final String id, displayName, matchMakerId, serverFilterId;
    private final List<QueueEntry> inQueue;
    private final int maxTeamSize;
    private final int minTeamSize;
    private final int numberOfTeams;

    public Queue(String id, String displayName, String matchMakerId, String serverFilterId, int maxTeamSize, int minTeamSize, int numberOfTeams) {
        this.id = id;
        this.displayName = displayName;
        this.matchMakerId = matchMakerId;
        this.serverFilterId = serverFilterId;
        this.inQueue = new ArrayList<>();
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSize;
        this.numberOfTeams = numberOfTeams;
    }

    public void addToQueue(UUID playerId) {
        if (inQueue.stream().anyMatch(queueEntry -> queueEntry.getPlayers().contains(playerId)))
            throw new IllegalArgumentException("Player already in queue");
        inQueue.add(QueueEntry.create(playerId));
    }

    public int getQueueSize() {
        return inQueue.stream().mapToInt(queueEntry -> queueEntry.getPlayers().size()).sum();
    }

    public QueueEntry removeFromQueue(UUID playerId) {
        Optional<QueueEntry> queueEntry = inQueue.stream().filter(entry -> entry.getPlayers().contains(playerId)).findFirst();
        if (queueEntry.isEmpty()) throw new IllegalArgumentException("Player not in queue");
        inQueue.remove(queueEntry.get());
        return queueEntry.get();
    }
}
