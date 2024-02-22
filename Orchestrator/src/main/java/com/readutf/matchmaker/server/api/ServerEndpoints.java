package com.readutf.matchmaker.server.api;

import com.readutf.matchmaker.api.annotation.GET;
import com.readutf.matchmaker.api.annotation.MappingPath;
import com.readutf.matchmaker.api.annotation.RestEndpoint;
import com.readutf.matchmaker.shared.server.Server;
import com.readutf.matchmaker.server.ServerManager;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
@RestEndpoint("/server")
public class ServerEndpoints {

    private final ServerManager serverManager;

    @GET
    @MappingPath("/list")
    public Collection<Server> getServers() {
        return serverManager.getServers().stream().map(registeredServer -> (Server) registeredServer).toList();
    }

}
