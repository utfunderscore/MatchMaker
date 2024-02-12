package com.readutf.matchmaker.network;

import com.readutf.matchmaker.packet.PacketDecoder;
import com.readutf.matchmaker.packet.PacketEncoder;
import com.readutf.matchmaker.packet.PacketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class NetworkManager {

    private static final Logger logger = LoggerFactory.getLogger(NetworkManager.class);

    private final PacketManager packetManager;
    private final ExecutorService service;

    public NetworkManager(ExecutorService service, PacketManager packetManager) {
        this.service = service;
        this.packetManager = packetManager;
    }

    public Channel startConnection(String address, int port) {

        CompletableFuture<Channel> channelFuture = new CompletableFuture<>();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        service.submit(() -> {
            try {
                ServerBootstrap b = new ServerBootstrap();


                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline().addLast(
                                        new PacketDecoder(packetManager),
                                        new PacketEncoder(packetManager),
                                        new ServerInboundHandler(packetManager)
                                );
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                try {
                    ChannelFuture f = b.bind(address, port).sync();

                    logger.info("Orchestrator started on " + address + ":" + port);
                    channelFuture.complete(f.channel());

                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    channelFuture.completeExceptionally(e);
                }
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        });


        try {
            return channelFuture.get();
        } catch (ExecutionException | InterruptedException e) {
           throw new RuntimeException(e);
        }
    }

}
