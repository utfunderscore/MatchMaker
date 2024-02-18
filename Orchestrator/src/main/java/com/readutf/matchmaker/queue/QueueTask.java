package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.api.socket.SocketManager;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.matches.MatchManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class QueueTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(QueueTask.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final QueueManager queueManager;
    private final MatchManager matchManager;
    private final WebSocket listenerSocket;

    @Override
    public void run() {

        for (Queue queue : queueManager.getQueues()) {

            executorService.submit(() -> {

                String matchMakerId = queue.getMatchMakerId();
                MatchMaker matchMaker = queueManager.getMatchMaker(matchMakerId);
                if(matchMaker == null) {
                    logger.warn("MatchMaker not found for queue: " + queue.getName());
                    return;
                }

                try {
                    List<List<UUID>> teams = matchMaker.onIteration(queue.getInQueue());



                } catch (Exception e) {

                    return;
                }

//                Predicate<Server> filter = queueManager.getFilter(queue.getServerFilterId());
//                if(filter == null) {
//                    System.out.println("Filter not found");
//                    return;
//                }
//
//                MatchRequestResult future = matchManager.requestMatch(queue.getQueueId().toString(), filter, teams, 3).join();
//                if(future.isFailure()) {
//                    System.out.println("Failed to find a match");
//                    return;
//                }
//
//
//
//                queue.getInQueue().removeIf(queueEntry -> queueEntry.getPlayers().stream().anyMatch(uuid -> teams.stream().flatMap(Collection::stream).toList().contains(uuid)));

            });

        }

    }
}
