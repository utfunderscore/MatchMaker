package com.readutf.matchmaker.matches.api;

import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.matches.MatchManager;
import io.javalin.http.Context;
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

            CompletableFuture.allOf(
                    IntStream.range(0, 500).mapToObj(i -> matchManager.requestMatch("test", List.of())).toArray(CompletableFuture[]::new)
            ).get();

            long end = System.currentTimeMillis();

            long duration = end - start;
            System.out.println("took: " + duration + "ms");

            context.json(duration);
        };
    }

}
