package com.readutf.matchmaker.api.socket;

import io.javalin.Javalin;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SocketManager {

    private static final Logger logger = LoggerFactory.getLogger(SocketManager.class);

    private final Javalin javalin;
    private final List<WebSocket> webSockets;

    public SocketManager(Javalin javalin) {
        this.javalin = javalin;
        this.webSockets = new ArrayList<>();
    }

    public WebSocket registerSocket(WebSocket webSocket) {
        webSockets.add(webSocket);


        javalin.ws(webSocket.getPath(), wsConfig -> {
            wsConfig.onConnect(wsContext -> {
                logger.info("WebSocket connected");
                wsContext.session.setIdleTimeout(Duration.ofMillis(-1));
                webSocket.getContexts().add(wsContext);
                webSocket.onConnect(wsContext);
            });
            wsConfig.onMessage(webSocket::onMessage);
            wsConfig.onClose(wsContext -> {
                logger.info("WebSocket closed");
                webSocket.getContexts().remove(wsContext);
                webSocket.onClose(wsContext);
            });
        });

        return webSocket;
    }
}
