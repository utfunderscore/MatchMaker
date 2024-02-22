package com.readutf.matchmaker.shared.packet.packets;

import com.readutf.matchmaker.shared.packet.Packet;
import com.readutf.matchmaker.shared.server.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
