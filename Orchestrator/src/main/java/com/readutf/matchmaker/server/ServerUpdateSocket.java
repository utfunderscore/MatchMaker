package com.readutf.matchmaker.server;

import com.google.gson.Gson;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import io.javalin.websocket.WsContext;

public class ServerUpdateSocket extends WebSocket {

    private final ServerUpdateManager serverUpdateManager;

    public ServerUpdateSocket(Gson gson, ServerUpdateManager serverUpdateManager) {
        super(gson,"/serverinfo/{category}");
        this.serverUpdateManager = serverUpdateManager;
    }

    @Override
    public void onConnect(WsContext wsContext) {
        String[] categories = wsContext.pathParam("category").split(",");

        for (String category : categories) {
            serverUpdateManager.registerCategoryListener(category, wsContext);
        }
    }

}
