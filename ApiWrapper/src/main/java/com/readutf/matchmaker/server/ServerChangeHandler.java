package com.readutf.matchmaker.server;

import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.shared.server.ServerUpdate;

import java.util.List;

public interface ServerChangeHandler {

    void handleUpdate(ServerUpdate<?> servers);

}
