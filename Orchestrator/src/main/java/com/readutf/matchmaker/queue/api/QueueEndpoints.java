package com.readutf.matchmaker.queue.api;

import com.readutf.matchmaker.api.annotation.GET;
import com.readutf.matchmaker.api.annotation.MappingPath;
import com.readutf.matchmaker.api.annotation.PUT;
import com.readutf.matchmaker.api.annotation.RestEndpoint;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.queue.Queue;
import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterData;
import io.javalin.websocket.WsContext;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestEndpoint("/queue")
@RequiredArgsConstructor
public class QueueEndpoints {

    private final QueueManager queueManager;

    @PUT
    @MappingPath("/create")
    public Queue createQueue(String queueName, String matchMakerId, String filterId, int maxTeamSize, int minTeamSize, int numberOfTeams) throws Exception {
        return queueManager.createQueue(queueName, matchMakerId, filterId, maxTeamSize, minTeamSize, numberOfTeams);
    }

    @GET
    @MappingPath("/list")
    public Collection<Queue> listQueues() {
        return queueManager.getQueues();
    }

    @PUT
    @MappingPath("/filter/create")
    public ServerFilterData createFilter(String name, String typeId, List<String> parameters) throws Exception {
        return queueManager.registerFilter(new ServerFilterData(name, typeId, parameters));
    }

    @PUT
    @MappingPath("/add")
    public void addToQueue(String queueName, String playerId) {
        queueManager.addToQueue(queueName, UUID.fromString(playerId));
    }

}
