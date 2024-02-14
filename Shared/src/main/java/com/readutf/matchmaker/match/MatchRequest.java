package com.readutf.matchmaker.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Getter @ToString
public class MatchRequest {

    private final UUID requestId;
    private final String queueId;
    private final List<List<UUID>> teams;

}
