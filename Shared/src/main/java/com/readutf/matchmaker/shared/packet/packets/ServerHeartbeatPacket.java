package com.readutf.matchmaker.shared.packet.packets;

import com.readutf.matchmaker.shared.packet.Packet;
import com.readutf.matchmaker.shared.server.ServerHeartbeat;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Sent by a client to the orchestration server to update states of their servers
 * Should be sent periodically to keep the orchestration server up to date
 */
@Getter @AllArgsConstructor
public class ServerHeartbeatPacket extends Packet {

    ServerHeartbeat serverHeartbeat;

}
