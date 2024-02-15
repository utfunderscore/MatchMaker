package com.readutf.matchmaker.server.api;

import com.readutf.matchmaker.server.ServerManager;
import io.javalin.http.Handler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerEndpoints {

    private final ServerManager serverManager;

    public Handler listServers() {
        return context -> context.json(serverManager.getServers());
    }

}
