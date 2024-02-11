package com.readutf.matchmaker.api;

import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.queue.api.QueueEndpoints;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import com.readutf.matchmaker.server.socket.ServerUpdateSocket;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

public class EndpointManager {

    public EndpointManager(QueueManager queueManager, ServerUpdateManager serverUpdateManager) {
        QueueEndpoints queueEndpoints = new QueueEndpoints(queueManager);

        Javalin
                .create(config -> config.jsonMapper(new JavalinGson()))
                .ws("/serverinfo/{category}", ws -> ws.onConnect(new ServerUpdateSocket(serverUpdateManager)))
                .get("/queue/list", queueEndpoints.getQueues())
                .put("/queue/filter", queueEndpoints.createFilter())
                .start(8080);
    }
}
