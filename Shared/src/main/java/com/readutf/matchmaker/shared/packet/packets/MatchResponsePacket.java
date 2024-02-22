package com.readutf.matchmaker.shared.packet.packets;

import com.readutf.matchmaker.shared.match.MatchResponse;
import com.readutf.matchmaker.shared.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter @ToString
public class MatchResponsePacket extends Packet {

    private final MatchResponse matchResponse;

}
