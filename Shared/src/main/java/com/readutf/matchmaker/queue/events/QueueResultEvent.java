package com.readutf.matchmaker.queue.events;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.queue.QueueEvent;
import com.readutf.matchmaker.server.Server;
import lombok.ToString;

import java.util.List;

@ToString
public class QueueResultEvent extends QueueEvent {

    private final Server server;
    private final MatchRequest matchRequest;
    private final List<MatchResponse> responses;

    public QueueResultEvent(String queueId, Server server, MatchRequest matchRequest, List<MatchResponse> responses) {
        super(queueId);
        this.server = server;
        this.matchRequest = matchRequest;
        this.responses = responses;
    }

    public int getAttempts() {
        return responses.size();
    }

    public boolean isFailure() {
        return server == null || matchRequest == null;
    }

}
