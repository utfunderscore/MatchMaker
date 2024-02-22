package com.readutf.matchmaker.server.socket;

import com.google.gson.reflect.TypeToken;
import com.readutf.matchmaker.shared.server.Server;
import io.javalin.websocket.WsContext;

import java.util.*;

/**
 * Stores a list of servers that have been updated and sends them to the appropriate websockets.
 * When a websocket is registered, a category is specified and all servers related to that category
 * will be sent to the websocket on update
 */
public class ServerUpdateManager extends TimerTask {

    private final HashMap<String, Set<Server>> updated;
    private final HashMap<String, List<WsContext>> categoryToSockets;

    public ServerUpdateManager(Timer timer) {
        this.updated = new HashMap<>();
        this.categoryToSockets = new HashMap<>();
        timer.scheduleAtFixedRate(this, 0, 5000);
    }

    @Override
    public void run() {
        HashMap<String, Set<Server>> latest = new HashMap<>(updated);
        updated.clear();

        latest.forEach((s, servers) -> {

            List<WsContext> contexts = categoryToSockets.getOrDefault(s, new ArrayList<>());
            contexts.addAll(categoryToSockets.getOrDefault("*", new ArrayList<>()));

            for (WsContext context : contexts) {
                context.sendAsClass(servers, new TypeToken<Set<Server>>() {}.getType());
            }

        });


    }

    public void notifyChange(Server server) {
        Set<Server> servers = updated.getOrDefault(server.getCategory(), new HashSet<>());
        servers.add(server);
        updated.put(server.getCategory(), servers);
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
