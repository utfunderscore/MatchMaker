package com.readutf.matchmaker.queue.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readutf.matchmaker.queue.QueueManager;
import com.readutf.matchmaker.queue.serverfilter.ServerFilterData;
import io.javalin.http.Handler;
import io.javalin.websocket.WsConnectHandler;

public class QueueEndpoints {

    private final Gson gson;
    private final QueueManager queueManager;

    public QueueEndpoints(QueueManager queueManager) {
        this.queueManager = queueManager;
        this.gson = new Gson();
    }

    public Handler getQueues() {
        return ctx -> ctx.json(queueManager.getQueues());
    }

    public Handler createFilter() {
        return ctx -> {
            String filterJson = ctx.queryParam("filter");
            ServerFilterData serverFilterData = gson.fromJson(filterJson, new TypeToken<ServerFilterData>(){}.getType());
            ctx.json(queueManager.registerFilter(serverFilterData));
        };
    }

    public Handler listFilters() {
        return ctx -> ctx.json(queueManager.getServerFilters());
    }

    public WsConnectHandler queueListener() {
        return wsConnectContext -> {

        };
    }

}
