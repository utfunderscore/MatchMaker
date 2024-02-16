package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.matches.MatchRequestResult;
import com.readutf.matchmaker.server.Server;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class QueueTask extends TimerTask {

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final QueueManager queueManager;
    private final MatchManager matchManager;

    @Override
    public void run() {

        for (Queue queue : queueManager.getQueues()) {

            executorService.submit(() -> {

                String matchMakerId = queue.getMatchMakerId();
                MatchMaker matchMaker = queueManager.getMatchMaker(matchMakerId);
                if(matchMaker == null) {
                    System.out.println("MatchMaker not found");
                    return;
                }

                List<List<UUID>> teams = matchMaker.onIteration(queue.getInQueue());

                Predicate<Server> filter = queueManager.getFilter(queue.getServerFilterId());
                if(filter == null) {
                    System.out.println("Filter not found");
                    return;
                }

                MatchRequestResult future = matchManager.requestMatch(queue.getQueueId().toString(), filter, teams, 3).join();
                if(future.isFailure()) {
                    System.out.println("Failed to find a match");
                    return;
                }

                queue.getInQueue().removeIf(queueEntry -> queueEntry.getPlayers().stream().anyMatch(uuid -> teams.stream().flatMap(Collection::stream).toList().contains(uuid)));

            });

        }

    }
}
