package com.readutf.matchmaker.server.socket;

import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ServerUpdateSocket implements WsConnectHandler {

    private final ServerUpdateManager serverUpdateManager;

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) {
        String category = wsConnectContext.pathParam("category");

        wsConnectContext.send("category: " + category);
        serverUpdateManager.registerCategoryListener(category, wsConnectContext);
    }

}
