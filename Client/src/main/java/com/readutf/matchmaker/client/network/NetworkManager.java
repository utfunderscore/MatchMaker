package com.readutf.matchmaker.client.network;

import com.readutf.matchmaker.shared.packet.PacketDecoder;
import com.readutf.matchmaker.shared.packet.PacketEncoder;
import com.readutf.matchmaker.shared.packet.PacketManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Getter
public class NetworkManager {

    private final ExecutorService executor;
    private final String host;
    private final int port;
    private final PacketManager packetManager;
    private @Setter Channel channel;


    public NetworkManager(ExecutorService executor, String host, int port, PacketManager packetManager) {
        this.executor = executor;
        this.host = host;
        this.port = port;
        this.packetManager = packetManager;
        this.channel = startConnection();
    }

    public Channel startConnection() {

        CompletableFuture<Channel> future = new CompletableFuture<>();


        executor.submit(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap bootstrap = new Bootstrap()
                        .group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline().addLast(
                                        new PacketEncoder(packetManager),
                                        new PacketDecoder(packetManager),
                                        new InboundPacketHandler(packetManager)
                                );
                            }
                        });

                ChannelFuture f = bootstrap.connect(host, port).sync();

                future.complete(f.channel());

                packetManager.setChannel(f.channel());

                f.channel().closeFuture().sync();
                System.out.println("closed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
