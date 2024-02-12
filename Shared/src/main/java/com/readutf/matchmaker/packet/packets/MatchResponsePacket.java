package com.readutf.matchmaker.packet.packets;

import com.readutf.matchmaker.match.MatchResponse;
import com.readutf.matchmaker.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class MatchResponsePacket extends Packet {

    private final MatchResponse matchResponse;

}
