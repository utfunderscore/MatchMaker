package com.readutf.matchmaker.packet.packets;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class MatchRequestPacket extends Packet {

    private MatchRequest matchRequest;

}
