package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.ErosServer;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.shared.queue.QueueEntry;
import com.readutf.matchmaker.shared.queue.QueueEvent;
import com.readutf.matchmaker.shared.queue.events.QueueErrorEvent;
import com.readutf.matchmaker.shared.server.Server;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@Getter
public class QueueTask extends TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(QueueTask.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final QueueManager queueManager;
    private final MatchManager matchManager;
    private final WebSocket listenerSocket;

    public QueueTask(QueueManager queueManager, MatchManager matchManager, WebSocket listenerSocket) {
        this.queueManager = queueManager;
        this.matchManager = matchManager;
        this.listenerSocket = listenerSocket;
        ErosServer.getTimer().scheduleAtFixedRate(this, 0, 1000);
    }

    @Override
    public void run() {

        for (Queue queue : queueManager.getQueues()) {

//            executorService.submit(() -> {

            long start = System.currentTimeMillis();

            String matchMakerId = queue.getMatchMakerId();
            MatchMaker matchMaker = queueManager.getMatchMaker(matchMakerId);
            if (matchMaker == null) {
                logger.warn("MatchMaker not found for queue: " + queue.getName());
                return;
            }

            if (queue.getQueueSize() < queue.getMinTeamSize() * queue.getNumberOfTeams()) return;


            try {
                List<List<UUID>> teams = matchMaker.onIteration(queue, queue.getInQueue());

                for (QueueEntry queueEntry : queue.getInQueue()) {
                    for (UUID player : queueEntry.getPlayers()) {
                        queueManager.getPlayerToQueue().remove(player);
                    }
                }
                queue.getInQueue().removeIf(queueEntry -> queueEntry.getPlayers().stream().anyMatch(uuid -> teams.stream().flatMap(Collection::stream).toList().contains(uuid)));


                Predicate<Server> filter = queueManager.getFilter(queue.getServerFilterId());
                if (filter == null) {
                    throw new Exception("Filter not found for queue: " + queue.getName());
                }

                logger.info("Requesting match for queue: " + queue.getName() + " with teams: " + teams.size() + " in " + (System.currentTimeMillis() - start) + "ms");
                logger.info("FilterId: " + filter);


                matchManager.requestMatch(queue.getName(), filter, teams, 3)
                        .thenAccept(queueResultEvent -> {
                            try {
                                System.out.println("Match result: " + queueResultEvent);
                                listenerSocket.send(queueResultEvent, QueueEvent.class);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });


            } catch (Exception e) {

                e.printStackTrace();

                listenerSocket.send(new QueueErrorEvent(queue.getName(), e.getMessage()), QueueEvent.class);
                logger.error("Failed to find a match for queue: " + queue.getName(), e);

                for (QueueEntry queueEntry : new ArrayList<>(queue.getInQueue())) {
                    for (UUID player : queueEntry.getPlayers()) {
                        queueManager.removeFromQueue(player);
                    }
                }
                queue.getInQueue().clear();
            }

            logger.debug("QueueTask took " + (System.currentTimeMillis() - start) + "ms to run");

        }

    }
}
