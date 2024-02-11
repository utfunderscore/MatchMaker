package com.readutf.matchmaker.packet.packets;

import com.readutf.matchmaker.packet.Packet;
import com.readutf.matchmaker.server.ServerHeartbeat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Sent by a client to the orchestration server to update states of their servers
 * Should be sent periodically to keep the orchestration server up to date
 */
@Getter @AllArgsConstructor
public class ServerHeartbeatPacket extends Packet {

    List<ServerHeartbeat> serverHeartbeats;

}
