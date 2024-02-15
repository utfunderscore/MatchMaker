package com.readutf.matchmaker.match;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MatchResponse {

    private UUID requestId, matchId;
    private boolean successful;
    private String failureReason;

    private MatchResponse(UUID requestId, UUID matchId, boolean successful, String failureReason) {
        this.requestId = requestId;
        this.matchId = matchId;
        this.successful = successful;
        this.failureReason = failureReason;
    }

    @Override
    public String toString() {
        return "MatchResponse{" +
                "requestId=" + requestId +
                ", successful=" + successful +
                ", failureReason='" + failureReason + '\'' +
                '}';
    }

    public static MatchResponse success(UUID requestId, UUID matchId) {
        return new MatchResponse(requestId, matchId, true, null);
    }

    public static MatchResponse failure(UUID requestId, String reason) {
        return new MatchResponse(requestId, null, false, reason);
    }

}
