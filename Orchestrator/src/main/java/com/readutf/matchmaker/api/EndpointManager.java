package com.readutf.matchmaker.api;

import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import com.readutf.matchmaker.server.socket.ServerUpdateSocket;
import io.javalin.Javalin;
import io.javalin.json.JavalinGson;

public class EndpointManager {

    public EndpointManager(ServerUpdateManager serverUpdateManager) {
        Javalin
                .create(config -> config.jsonMapper(new JavalinGson()))
                .ws("/serverinfo/{category}", ws -> ws.onConnect(new ServerUpdateSocket(serverUpdateManager)))
                .start(8080);
    }
}
