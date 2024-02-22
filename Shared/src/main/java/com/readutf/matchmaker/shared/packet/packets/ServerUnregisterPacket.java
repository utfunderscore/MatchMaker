package com.readutf.matchmaker.shared.packet.packets;

import com.readutf.matchmaker.shared.packet.Packet;
import lombok.Getter;

import java.util.UUID;

/**
 * Sent by client servers to the orchestration server to unregister themselves
 */
@Getter
public class ServerUnregisterPacket extends Packet {

    private final UUID serverId;

    public ServerUnregisterPacket(UUID serverId) {
        this.serverId = serverId;
    }

}
