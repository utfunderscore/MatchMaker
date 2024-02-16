package com.readutf.matchmaker.api;

import com.readutf.matchmaker.ErosServer;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.matches.api.MatchEndpoints;
import com.readutf.matchmaker.queue.api.QueueEndpoints;
import com.readutf.matchmaker.server.api.ServerEndpoints;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import com.readutf.matchmaker.server.socket.ServerUpdateSocket;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

public class EndpointManager {

    public EndpointManager(ErosServer erosServer) {
        QueueEndpoints queueEndpoints = new QueueEndpoints(erosServer.getQueueManager());
        MatchEndpoints matchEndpoints = erosServer.getMatchManager().getMatchEndpoints();
        ServerUpdateManager serverUpdateManager = erosServer.getServerUpdateManager();
        MatchManager matchManager = erosServer.getMatchManager();
        ServerEndpoints serverEndpoints = new ServerEndpoints(erosServer.getServerManager());

        Javalin
                .create(config -> {
                    config.jsonMapper(new JavalinGson());
                })
                .ws("/serverinfo/{category}", ws -> {
                    ws.onConnect(new ServerUpdateSocket(serverUpdateManager));
                })
                .get("/queue/list", queueEndpoints.getQueues())
                .put("/queue/filter", queueEndpoints.createFilter())
                .get("/queue/filters", queueEndpoints.listFilters())
                .get("/match/create", matchManager.getMatchEndpoints().createMatch())
                .get("/server/list", serverEndpoints.listServers())
                .start(8080);
    }
}
