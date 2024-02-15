package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.server.Server;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ToString
public class MatchRequestResult {

    private final @Nullable Server server;
    private final @Nullable MatchRequest matchRequest;
    private final List<MatchResponse> responses;

    public MatchRequestResult(@Nullable Server server, @Nullable MatchRequest matchRequest, @NotNull List<MatchResponse> responses) {
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
