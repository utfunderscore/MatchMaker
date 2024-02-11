package com.readutf.matchmaker.server;

import com.readutf.matchmaker.packet.serializable.Serializable;
import com.readutf.matchmaker.packet.utils.EasyByteWriter;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Heartbeat data sent by a server to the orchestration server
 */
@AllArgsConstructor @NoArgsConstructor @Getter
public class ServerHeartbeat implements Serializable {

    private UUID serverId;
    private int playerCount;

    /**
     * Encodes the heartbeat data into a byte buffer
     * Total size: 24 bytes
     */
    @Override
    public void encode(ByteBuf byteBuf) {
        new EasyByteWriter(byteBuf)
                .writeUUID(serverId)
                .writeInt(playerCount);
    }

    /**
     * Decodes the heartbeat data from a byte buffer
     */
    @Override
    public void decode(ByteBuf byteBuf) {
        this.serverId = new UUID(byteBuf.readLong(), byteBuf.readLong());
        this.playerCount = byteBuf.readInt();
    }

    @Override
    public String toString() {
        return "ServerHeartbeat{" +
                "serverId=" + serverId +
                ", playerCount=" + playerCount +
                '}';
    }
}
