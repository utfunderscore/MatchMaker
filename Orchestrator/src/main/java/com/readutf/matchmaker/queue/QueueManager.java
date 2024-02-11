package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.queue.serverfilter.InbuiltFilters;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterCreator;
import com.readutf.matchmaker.server.serverfilter.ServerFilterData;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterStore;
import com.readutf.matchmaker.server.Server;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class QueueManager {

    private final ServerFilterStore serverFilterStore;
    private final Map<String, MatchMaker> matchMakers;
    private final Map<String, ServerFilterCreator> serverFilterCreators;
    private final Map<String, ServerFilterData> serverFilters;
    private final Map<String, Queue> queues = new HashMap<>();

    public QueueManager() {
        this.serverFilterStore = new ServerFilterStore(new File(System.getProperty("user.dir")));
        this.matchMakers = new HashMap<>();
        this.serverFilterCreators = Map.of(
                "category", InbuiltFilters.getCategoryFilter()
        );
        this.serverFilters = serverFilterStore.loadAll();
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

    public Map<String, MatchMaker> getMatchMakers() {
        return Collections.unmodifiableMap(matchMakers);
    }

}
