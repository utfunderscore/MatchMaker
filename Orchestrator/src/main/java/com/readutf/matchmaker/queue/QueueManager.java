package com.readutf.matchmaker.queue;

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
    private final Map<UUID, Queue> queues = new HashMap<>();
    private final QueueStore queueStore;

    @SneakyThrows
    public QueueManager(File baseDir) {
        this.serverFilterStore = new ServerFilterStore(baseDir);
        this.matchMakers = new HashMap<>();
        this.queueStore = new FlatFileQueueStore(baseDir);
        this.serverFilterCreators = Map.of(
                "category", InbuiltFilters.getCategoryFilter()
        );
        this.serverFilters = serverFilterStore.loadAll();
        this.queueStore.loadQueues().forEach(queue -> queues.put(queue.getQueueId(), queue));
    }

    public Queue createQueue(String queueName, String matchMakerId, String filterId) throws Exception {
        if(!matchMakers.containsKey(matchMakerId)) throw new Exception("MatchMaker does not exist");

        Queue queue = new Queue(queueName, matchMakerId, filterId, 1, 1, 1);
        queues.put(queue.getQueueId(), queue);
        queueStore.saveQueues(queues.values());
        return queue;
    }

    public void registerMatchMaker(String name, MatchMaker matchMaker) {
        matchMakers.put(name.toLowerCase(), matchMaker);
    }

    public String registerFilter(ServerFilterData serverFilterData) {
        try {
            if(serverFilters.containsKey(serverFilterData.getFilterName())) return "Filter already exists";
            serverFilters.put(serverFilterData.getFilterName(), serverFilterData);
            serverFilterStore.saveFilters(serverFilters);
            return "Filter created";
        } catch (Exception e) {
            return "Failed to create filter: " + e.getMessage();
        }
    }

    public Predicate<Server> getFilter(String filterId) {
        ServerFilterData serverFilterData = serverFilters.get(filterId);
        ServerFilterCreator creator = serverFilterCreators.get(serverFilterData.getServerFilterCreatorId());
        try {
            return creator.createFilter(serverFilterData.getArguments().toArray(new String[0]));
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<Queue> getQueues() {
        return queues.values();
    }

    public Map<String, ServerFilterData> getServerFilters() {
        return Collections.unmodifiableMap(serverFilters);
    }

    public @Nullable MatchMaker getMatchMaker(String matchMakerId) {
        return matchMakers.get(matchMakerId);
    }
}
