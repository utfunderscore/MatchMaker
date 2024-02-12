package com.readutf.matchmaker.server;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class RegisteredServer extends Server {

    private transient final Channel channel;

    public RegisteredServer(Channel channel, Server server) {
        this(server.getId(), server.getActiveGames(), server.getMaxGames(), server.getAddress(), server.getCategory(),
                server.getPort(), server.getLastHeartbeat(), server.getAttributes(), channel);
    }

    public RegisteredServer(UUID id, int activeGames, int maxGames, String address, String category, int port, long lastHeartbeat, Map<String, ServerAttribute> attributes, Channel channel) {
        super(id, activeGames, maxGames, address, category, port, lastHeartbeat, attributes);
        this.channel = channel;
    }

    public boolean isUnreachable() {
        return System.currentTimeMillis() - getLastHeartbeat() > 10000;
    }

    public boolean handleHeartbeat(ServerHeartbeat heartbeat) {
        if(getActiveGames() != heartbeat.getPlayerCount()) {
            setActiveGames(heartbeat.getPlayerCount());
            setLastHeartbeat(System.currentTimeMillis());
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "RegisteredServer{" +
                "channel=" + channel.id().asShortText() +
                ", server=" + super.toString() +
                '}';
    }
}

