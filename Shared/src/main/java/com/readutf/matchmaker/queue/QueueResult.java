package com.readutf.matchmaker.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor @Getter
public class QueueResult {

    private List<List<UUID>> teams;
    private String failureReason;

    private QueueResult(List<List<UUID>> teams) {
        this.teams = teams;
    }

    private QueueResult(String failureReason) {
        this.failureReason = failureReason;
    }

    public boolean isSuccess() {
        return teams != null;
    }

    public static QueueResult success(List<List<UUID>> teams) {
        return new QueueResult(teams);
    }

    public static QueueResult failure(String failureReason) {
        return new QueueResult(failureReason);
    }

}
