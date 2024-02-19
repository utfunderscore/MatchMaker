package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.queue.events.QueuePlayerEvent;
import com.readutf.matchmaker.queue.impl.BasicMatchMaker;
import com.readutf.matchmaker.queue.serverfilter.InbuiltFilters;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterCreator;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterData;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterStore;
import com.readutf.matchmaker.queue.store.QueueStore;
import com.readutf.matchmaker.queue.store.impl.FlatFileQueueStore;
import com.readutf.matchmaker.server.Server;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;

public class QueueManager {

    private final ServerFilterStore serverFilterStore;

    private final Map<String, MatchMaker> matchMakers;
    private final Map<String, ServerFilterCreator> serverFilterCreators;
    private final Map<String, ServerFilterData> serverFilters;
    private final Map<String, Queue> queues = new HashMap<>();
    private final QueueStore queueStore;

    private QueueTask queueTask;

    @SneakyThrows
    public QueueManager(File baseDir) {
        this.serverFilterStore = new ServerFilterStore(baseDir);
        this.queueStore = new FlatFileQueueStore(baseDir);
        this.serverFilterCreators = Map.of(
                "category", InbuiltFilters.getCategoryFilter()
        );
        this.matchMakers = Map.of(
                "simple", new BasicMatchMaker()
        );
        this.serverFilters = serverFilterStore.loadAll();
        this.queueStore.loadQueues().forEach(queue -> queues.put(queue.getName(), queue));
    }

    public QueueTask startQueueTask(MatchManager matchManager, WebSocket queueSocket) {
        if (queueTask != null) throw new IllegalStateException("Queue task already running");

        queueTask = new QueueTask(this, matchManager, queueSocket);

        return queueTask;
    }

    public Queue createQueue(String queueName, String matchMakerId, String filterId, int maxTeamSize, int minTeamSize, int numberOfTeams) throws Exception {
        if (!matchMakers.containsKey(matchMakerId)) throw new Exception("MatchMaker does not exist");

        Queue queue = new Queue(queueName, matchMakerId, filterId, maxTeamSize, minTeamSize, numberOfTeams);
        queues.put(queue.getName(), queue);
        queueStore.saveQueues(queues.values());
        return queue;
    }

    public void addToQueue(String queueName, UUID playerId) {
        Queue orDefault = queues.getOrDefault(queueName, null);
        if (orDefault == null) throw new IllegalArgumentException("Queue does not exist");
        MatchMaker matchMaker = matchMakers.get(orDefault.getMatchMakerId());
        if (matchMaker == null) throw new IllegalArgumentException("MatchMaker does not exist");
        if (!matchMaker.validateEntry(orDefault, QueueEntry.create(playerId)))
            throw new IllegalArgumentException("Queue entry does not meet matchmaker requirements");

        orDefault.addToQueue(playerId);
        queueTask.getListenerSocket().send(new QueuePlayerEvent(queueName, true, playerId));
    }

    public ServerFilterData registerFilter(ServerFilterData serverFilterData) throws Exception {

        ServerFilterCreator creator = serverFilterCreators.get(serverFilterData.getCreatorId());
        if (creator == null) {
            throw new Exception("Filter type does not exist");
        }
        creator.createFilter(serverFilterData.getArguments().toArray(new String[0]));

        serverFilters.put(serverFilterData.getFilterName(), serverFilterData);
        serverFilterStore.saveFilters(serverFilters);
        return serverFilterData;
    }

    public Predicate<Server> getFilter(String filterId) {
        ServerFilterData serverFilterData = serverFilters.get(filterId);
        ServerFilterCreator creator = serverFilterCreators.get(serverFilterData.getCreatorId());
        try {
            return creator.createFilter(serverFilterData.getArguments().toArray(new String[0]));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create filter: " + e.getMessage());
        }
    }

    public Collection<Queue> getQueues() {
        return queues.values();
    }

    public boolean queueExists(String queueName) {
        return queues.containsKey(queueName);
    }

    public Map<String, ServerFilterData> getServerFilters() {
        return Collections.unmodifiableMap(serverFilters);
    }

    public @Nullable MatchMaker getMatchMaker(String matchMakerId) {
        return matchMakers.get(matchMakerId);
    }
}
