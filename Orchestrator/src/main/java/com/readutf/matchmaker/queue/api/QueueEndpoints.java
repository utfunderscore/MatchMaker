package com.readutf.matchmaker.queue.api;

import com.readutf.matchmaker.api.annotation.*;
import com.readutf.matchmaker.shared.api.ApiResponse;
import com.readutf.matchmaker.shared.queue.Queue;
import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.shared.queue.serverfilter.ServerFilterData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestEndpoint("/queue")
@RequiredArgsConstructor
public class QueueEndpoints {

    private static Logger logger = LoggerFactory.getLogger(QueueEndpoints.class);

    private final QueueManager queueManager;

    private static final ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    @PUT
    @MappingPath()
    public Queue createQueue(String queueName, String matchMakerId, String filterId, int maxTeamSize, int minTeamSize, int numberOfTeams) throws Exception {
        return queueManager.createQueue(queueName, matchMakerId, filterId, maxTeamSize, minTeamSize, numberOfTeams);
    }

    @GET()
    @MappingPath("/list")
    public Collection<Queue> listQueues() {
        return queueManager.getQueues();
    }

    @GET
    @MappingPath
    public Queue getQueue(String queueName) {
        return Objects.requireNonNull(queueManager.getQueue(queueName));
    }

    @DELETE
    @MappingPath
    public boolean deleteQueue(String queueName) {
        return queueManager.deleteQueue(queueName);
    }

    @PUT
    @MappingPath("/join")
    public boolean addToQueue(String queueName, String playerId) {
        synchronized (queueManager.getPlayerToQueue()) {
           queueManager.addToQueue(queueName, UUID.fromString(playerId));
        }
        return true;
    }

    @PUT
    @MappingPath("/leave")
    public void removeFromQueue(String playerId) {
        queueManager.removeFromQueue(UUID.fromString(playerId));
    }

    //////////////////////////////////////////////////////////////////////////////
    //FILTERS
    //////////////////////////////////////////////////////////////////////////////

    @GET
    @MappingPath("/filter")
    public Collection<ServerFilterData> listFilters() {
        return queueManager.getServerFilters().values();
    }

    @PUT
    @MappingPath("/filter")
    public ServerFilterData createFilter(String name, String typeId, List<String> parameters) throws Exception {
        return queueManager.registerFilter(new ServerFilterData(name, typeId, parameters));
    }

    @DELETE
    @MappingPath("/filter")
    public boolean deleteFilter(String name) {
        return queueManager.deleteFilter(name);
    }

}
