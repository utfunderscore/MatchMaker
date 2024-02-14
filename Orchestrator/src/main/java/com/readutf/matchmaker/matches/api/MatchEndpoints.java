package com.readutf.matchmaker.matches.api;

import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.matches.MatchRequestResult;
import io.javalin.http.Handler;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class MatchEndpoints {

    private final MatchManager matchManager;

    public Handler createMatch() {
        return context -> {
            long start = System.currentTimeMillis();

            CompletableFuture<MatchRequestResult> responses = matchManager.requestMatch("test", List.of(), 5);
            context.json(responses.join());
        };
    }

}
