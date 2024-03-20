package com.readutf.matchmaker.server.socket;

import com.google.gson.Gson;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.shared.server.ServerUpdate;
import io.javalin.websocket.WsContext;

import java.util.*;

/**
 * Stores a list of servers that have been updated and sends them to the appropriate websockets.
 * When a websocket is registered, a category is specified and all servers related to that category
 * will be sent to the websocket on update
 */
public class ServerUpdateManager extends TimerTask {

    private final Gson gson;
    private final HashMap<Server, ServerUpdate<?>> serverUpdates;
    private final HashMap<String, List<WsContext>> categoryToSockets;

    public ServerUpdateManager(Gson gson, Timer timer) {
        this.gson = gson;
        this.serverUpdates = new HashMap<>();
        this.categoryToSockets = new HashMap<>();
        timer.scheduleAtFixedRate(this, 0, 5000);
    }

    @Override
    public void run() {

        new HashMap<>(serverUpdates).forEach((server, serverUpdate) -> {
            List<WsContext> sockets = categoryToSockets.getOrDefault(server.getCategory(), new ArrayList<>());
            sockets.addAll(categoryToSockets.getOrDefault("*", new ArrayList<>()));

            sockets.removeIf(wsContext -> !wsContext.session.isOpen());
            for (WsContext socket : sockets) {
                socket.send(gson.toJson(serverUpdate, ServerUpdate.class));
            }

            serverUpdates.remove(server);
        });

        serverUpdates.clear();
    }

    public void notifyChange(Server server, ServerUpdate<?> serverUpdate) {
        serverUpdates.put(server, serverUpdate);
    }

    public void registerGlobalListener(WsContext context) {
        List<WsContext> sockets = categoryToSockets.getOrDefault("*", new ArrayList<>());
        sockets.add(context);
        categoryToSockets.put("*", sockets);
    }

    public void registerCategoryListener(String category, WsContext context) {
        List<WsContext> sockets = categoryToSockets.getOrDefault(category, new ArrayList<>());
        sockets.add(context);
        categoryToSockets.put(category, sockets);
    }

}
