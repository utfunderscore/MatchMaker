package com.readutf.matchmaker.server;

import com.readutf.matchmaker.shared.server.Server;

import java.util.List;

public interface ServerChangeHandler {

    void handleLatestServers(List<Server> servers);

}
