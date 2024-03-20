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

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

        for (Queue queue : new ArrayList<>(queueManager.getQueues())) {
            if(queue.getInQueue().isEmpty()) continue;
            if(queue.getInQueue().size() < queue.getNumberOfTeams()) return;

            MatchMaker matchMaker = queueManager.getMatchMaker(queue.getMatchMakerId());
            if(matchMaker == null) {
                logger.error("MatchMaker " + queue.getMatchMakerId() + " not found for queue " + queue.getId());
                invalidateQueue("MatchMaker not found", queue);
                continue;
            }

            Predicate<Server> serverFilter = queueManager.getFilter(queue.getServerFilterId());
            if(serverFilter == null) {
                logger.error("ServerFilter " + queue.getServerFilterId() + " not found for queue " + queue.getId());
                invalidateQueue("ServerFilter not found", queue);
                continue;
            }

            List<List<UUID>> teams;
            try {
               teams = matchMaker.onIteration(queue, queue.getInQueue());
            } catch (Exception e) {
                logger.debug("Error while iterating queue " + queue.getId() + " with matchmaker " + matchMaker.getClass().getSimpleName(), e);
                continue;
            }



            matchManager.requestMatch(queue.getId(), serverFilter, teams, 3)
                    .thenAccept(queueResultEvent -> {
                        try {

                            for (List<UUID> team : teams) {
                                for (UUID uuid : team) {
                                    queueManager.removeFromQueue(uuid);
                                }
                            }

                            listenerSocket.send(queueResultEvent, QueueEvent.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }

    }

    public void invalidateQueue(String reason, Queue queue) {
        List<UUID> allPlayers = queue.getInQueue().stream().flatMap(queueEntry -> queueEntry.getPlayers().stream()).collect(Collectors.toList());
        listenerSocket.send(
                new QueueErrorEvent(queue.getId(),
                        allPlayers, reason),
                QueueEvent.class
        );

        for (UUID allPlayer : allPlayers) {
            queueManager.removeFromQueue(allPlayer);
        }

    }

}
