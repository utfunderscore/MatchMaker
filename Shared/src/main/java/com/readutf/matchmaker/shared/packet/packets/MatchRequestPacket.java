package com.readutf.matchmaker.shared.packet.packets;

import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class MatchRequestPacket extends Packet {

    private MatchRequest matchRequest;

}
