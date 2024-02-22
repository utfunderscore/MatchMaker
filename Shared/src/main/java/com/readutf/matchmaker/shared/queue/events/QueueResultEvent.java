package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import com.readutf.matchmaker.shared.server.Server;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString @Getter
public class QueueResultEvent extends QueueEvent {

    private final String queueId;
    private final Server server;
    private final MatchRequest matchRequest;
    private final List<MatchResponse> responses;

    public QueueResultEvent(String queueId, Server server, MatchRequest matchRequest, List<MatchResponse> responses) {
        this.queueId = queueId;
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
