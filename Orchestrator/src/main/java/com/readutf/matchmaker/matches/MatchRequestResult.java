package com.readutf.matchmaker.matches;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MatchRequestResult {

    private final @Nullable UUID serverId;
    private final @Nullable MatchRequest matchRequest;
    private final List<MatchResponse> responses;

    public MatchRequestResult(@Nullable UUID serverId, @Nullable MatchRequest matchRequest, @NotNull List<MatchResponse> responses) {
        this.serverId = serverId;
        this.matchRequest = matchRequest;
        this.responses = responses;
    }

    public int getAttempts() {
        return responses.size();
    }

    public boolean isFailure() {
        return serverId == null || matchRequest == null;
    }

}
