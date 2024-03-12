package com.readutf.matchmaker.matches.api;

import com.readutf.matchmaker.api.annotation.MappingPath;
import com.readutf.matchmaker.api.annotation.PUT;
import com.readutf.matchmaker.api.annotation.RestEndpoint;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.shared.queue.events.QueueResultEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestEndpoint("/matches")
public class MatchEndpoints {

    private final MatchManager matchManager;

    public MatchEndpoints(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @PUT
    @MappingPath("/create")
    public QueueResultEvent createMatch(String queueId, List<List<String>> teams, int maxAttempts) {

        List<List<UUID>> teamsParsed = teams.stream().map(uuids -> uuids.stream().map(UUID::fromString).toList()).toList();

        CompletableFuture<QueueResultEvent> matchFuture = matchManager.requestMatch(queueId, server -> true, teamsParsed, maxAttempts);
        return matchFuture.join();
    }

}
