package com.readutf.matchmaker.server;

import com.readutf.matchmaker.shared.server.Server;
import retrofit2.Retrofit;
import retrofit2.http.GET;

import java.util.List;

public interface ServerService {

    @GET("/server/list")
    List<Server> getServers();

    static ServerService builder(Retrofit retrofit) {
        return retrofit.create(ServerService.class);
    }

}
