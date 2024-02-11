package com.readutf.matchmaker.listeners;

import com.readutf.matchmaker.packet.annotations.PacketHandler;
import com.readutf.matchmaker.packet.packets.ServerRegisterPacket;

public class TestListener {

    @PacketHandler
    public void onHello(ServerRegisterPacket packet) {
        System.out.println("Hello World");
    }

}
