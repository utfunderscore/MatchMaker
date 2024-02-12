package com.readutf.matchmaker.server;

import com.readutf.matchmaker.packet.PacketManager;
import com.readutf.matchmaker.server.listeners.ServerListeners;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>
 * Manages all servers connected to the orchestrator
 * Servers represent individual game instances not necessarily individual servers,
 * For example, an individual client could manage 10+ servers on the same ip address.
 * Therefor, When connecting a user to a server, a join intent should be stored to determine the specific
 * server being connected to.
 * </p>
 */
public class ServerManager {

    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);

    private final Map<UUID, RegisteredServer> registeredServers;
    private final Map<Channel, List<RegisteredServer>> channelToServers;
    private final ServerUpdateManager serverUpdateManager;

    public ServerManager(ServerUpdateManager serverUpdateManager, @NotNull PacketManager packetManager) {
        this.registeredServers = new HashMap<>();
        this.channelToServers = new HashMap<>();
        this.serverUpdateManager = serverUpdateManager;
        packetManager.registerListeners(new ServerListeners(this));
    }

    /**
     * Register an existing server/game instance to be managed
     * @param channel - The tcp channel the server is connected via
     * @param server - The server instance
     */
    public void registerServer(Channel channel, @NotNull Server server) {
        RegisteredServer registeredServer = new RegisteredServer(channel, server);
        logger.info("Registered server " + registeredServer);

        this.registeredServers.put(server.getId(), registeredServer);

        this.channelToServers.compute(channel, (channel1, servers) -> {
            if (servers == null) servers = new ArrayList<>();
            servers.add(registeredServer);
            return servers;
        });
    }

    /**
     * Handle a heartbeat from a server
     * This is used to update the server's last heartbeat time and player count
     * TODO: More information will need to be added to this to handle more complex server states
     * @param uuid - The server's unique id
     * @param heartbeat - The server's heartbeat
     */
    public void handleHeartbeat(UUID uuid, ServerHeartbeat heartbeat) {

        logger.debug("Received heartbeat from " + uuid + " with " + heartbeat);

        RegisteredServer server = registeredServers.get(uuid);
        if (server == null) {
            logger.error("Received heartbeat from unregistered server: " + uuid);
            return;
        }

        if (server.handleHeartbeat(heartbeat)) {
            serverUpdateManager.notifyChange(server);
        }
    }

    /**
     * Unregister a server from the manager
     * @param registeredServer - The server to unregister
     */
    public void unregisterServer(@NotNull RegisteredServer registeredServer) {

        logger.info("Unregistered server " + registeredServer.getShortId());

        this.registeredServers.remove(registeredServer.getId());
        List<RegisteredServer> servers = this.channelToServers.get(registeredServer.getChannel());
        servers.remove(registeredServer);
        channelToServers.put(registeredServer.getChannel(), servers);
    }

    /**
     * Unregister a server from the manager
     * @param serverId - The id of the server to unregister
     */
    public void unregisterServer(UUID serverId) {
        RegisteredServer server = getServer(serverId);
        if (server == null) {
            logger.error("Tried to unregister non-existent server: " + serverId);
            return;
        }
        unregisterServer(server);
    }

    /**
     * Get a server by its unique id
     * @param serverId - The server's unique id
     * @return The server instance, or null if it doesn't exist
     */
    public @Nullable RegisteredServer getServer(UUID serverId) {
        return registeredServers.get(serverId);
    }

    public Collection<RegisteredServer> getServers() {
        return registeredServers.values();
    }

    /**
     * Get a server by its unique id
     * @param channel - The server's tcp channel
     * @return The server instance, or an empty array if it doesn't exist
     */
    public List<RegisteredServer> getServers(Channel channel) {
        return channelToServers.getOrDefault(channel, new ArrayList<>());
    }

}
