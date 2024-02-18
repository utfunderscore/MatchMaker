package com.readutf.matchmaker.server.socket;

import com.readutf.matchmaker.api.socket.WebSocket;
import io.javalin.websocket.WsContext;

public class ServerUpdateSocket extends WebSocket {

    private final ServerUpdateManager serverUpdateManager;

    public ServerUpdateSocket(ServerUpdateManager serverUpdateManager) {
        super("/serverinfo/{category}");
        this.serverUpdateManager = serverUpdateManager;
    }

    @Override
    public void onConnect(WsContext wsContext) {
        String category = wsContext.pathParam("category");
        wsContext.send("category: " + category);

        serverUpdateManager.registerCategoryListener(category, wsContext);
    }

}
