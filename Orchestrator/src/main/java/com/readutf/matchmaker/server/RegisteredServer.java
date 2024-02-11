package com.readutf.matchmaker.server;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RegisteredServer extends Server {

    private transient final Channel channel;

    public RegisteredServer(Channel channel, Server server) {
        this(server.getId(), server.getPlayerCount(), server.getAddress(), server.getCategory(), server.getPort(), server.getLastHeartbeat(), channel);
    }

    public RegisteredServer(UUID gameId, int playerCount, String address, String category, int port, long lastHeartbeat, Channel channel) {
        super(gameId, playerCount, address, category, port, lastHeartbeat);
        this.channel = channel;
    }

    public boolean isUnreachable() {
        return System.currentTimeMillis() - getLastHeartbeat() > 10000;
    }

    public boolean handleHeartbeat(ServerHeartbeat heartbeat) {
        if(getPlayerCount() != heartbeat.getPlayerCount()) {
            setPlayerCount(heartbeat.getPlayerCount());
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

