package com.readutf.matchmaker.match;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MatchResponse {

    private UUID requestId;
    private boolean successful;
    private String failureReason;

    public MatchResponse(UUID requestId, boolean successful, String failureReason) {
        this.requestId = requestId;
        this.successful = successful;
        this.failureReason = failureReason;
    }

    public static MatchResponse success(UUID requestId) {
        return new MatchResponse(requestId, true, null);
    }

    public static MatchResponse failure(UUID requestId, String reason) {
        return new MatchResponse(requestId, false, reason);
    }

}
