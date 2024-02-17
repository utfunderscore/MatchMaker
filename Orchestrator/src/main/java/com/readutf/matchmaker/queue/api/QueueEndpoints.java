package com.readutf.matchmaker.queue.api;

import com.readutf.matchmaker.api.annotation.GET;
import com.readutf.matchmaker.api.annotation.MappingPath;
import com.readutf.matchmaker.api.annotation.PUT;
import com.readutf.matchmaker.api.annotation.RestEndpoint;
import com.readutf.matchmaker.queue.Queue;
import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterData;
import com.readutf.matchmaker.utils.endpoint.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

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

}
