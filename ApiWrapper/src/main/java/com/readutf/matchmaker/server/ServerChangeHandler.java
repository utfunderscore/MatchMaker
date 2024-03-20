package com.readutf.matchmaker.server;

import com.readutf.matchmaker.shared.server.ServerUpdate;

public interface ServerChangeHandler {

    void handleUpdate(ServerUpdate<?> servers);

    void onOrchestrationClose();

}
