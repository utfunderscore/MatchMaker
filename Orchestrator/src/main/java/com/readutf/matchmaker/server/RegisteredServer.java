package com.readutf.matchmaker.server;

import io.netty.channel.Channel;
import lombok.Getter;

@Getter
public class RegisteredServer extends Server {

    private transient final Channel channel;

    public RegisteredServer(Channel channel, Server server) {
        super(server.getId(), server.getAddress(), server.getPort(), server.getCategory(), server.getMatches(), server.getAttributes(),
                server.getLastHeartbeat(), server.getActiveGames(), server.getMaxGames());
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "RegisteredServer{" +
                "channel=" + channel.id().asShortText() +
                ", server=" + super.toString() +
                '}';
    }
}

