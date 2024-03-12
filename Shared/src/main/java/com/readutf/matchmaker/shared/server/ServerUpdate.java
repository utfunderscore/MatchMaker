package com.readutf.matchmaker.shared.server;

import com.readutf.matchmaker.shared.utils.RuntimeTypeAdapterFactory;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class ServerUpdate<T> {

    private final T object;
    private final UpdateType updateType;

    private ServerUpdate(T object, UpdateType updateType) {
        this.object = object;
        this.updateType = updateType;
    }

    public enum UpdateType {
        ADD,
        REMOVE,
        UPDATE
    }

    public static RuntimeTypeAdapterFactory<ServerUpdate> getAdapter() {
        return RuntimeTypeAdapterFactory.of(ServerUpdate.class)
                .registerSubtype(ServerAddUpdate.class, "ServerAddUpdate")
                .registerSubtype(ServerRemoveUpdate.class, "ServerRemoveUpdate")
                .registerSubtype(ServerHeartbeatUpdate.class, "ServerUpdateUpdate");
    }

    public static ServerUpdate<Server> add(Server server) {
        return new ServerAddUpdate(server);
    }

    public static ServerUpdate<UUID> remove(UUID serverId) {
        return new ServerRemoveUpdate(serverId);
    }

    public static ServerUpdate<ServerHeartbeat> update(ServerHeartbeat serverHeartbeat) {
        return new ServerHeartbeatUpdate(serverHeartbeat);
    }

    public static class ServerAddUpdate extends ServerUpdate<Server> {

        private ServerAddUpdate(Server server) {
            super(server, UpdateType.ADD);
        }
    }

    public static class ServerRemoveUpdate extends ServerUpdate<UUID> {

        private ServerRemoveUpdate(UUID serverId) {
            super(serverId, UpdateType.REMOVE);
        }
    }

    public static class ServerHeartbeatUpdate extends ServerUpdate<ServerHeartbeat> {

        private ServerHeartbeatUpdate(ServerHeartbeat serverHeartbeat) {
            super(serverHeartbeat, UpdateType.UPDATE);
        }
    }

}
