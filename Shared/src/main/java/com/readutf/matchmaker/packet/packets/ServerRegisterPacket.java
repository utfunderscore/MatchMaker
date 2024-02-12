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

    private List<Server> availableServers;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ServerRegisterPacket that = (ServerRegisterPacket) object;
        return Objects.equals(availableServers, that.availableServers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availableServers);
    }
}
