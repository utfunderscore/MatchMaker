package com.readutf.matchmaker.packet.packets;

import com.readutf.matchmaker.packet.Packet;
import com.readutf.matchmaker.server.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;


/**
 * Sent by client servers to the orchestration server to register themselves
 */
@Getter @AllArgsConstructor
public class ServerRegisterPacket extends Packet {

    private Server server;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ServerRegisterPacket that)) return false;
        return Objects.equals(server, that.server);
    }
}
