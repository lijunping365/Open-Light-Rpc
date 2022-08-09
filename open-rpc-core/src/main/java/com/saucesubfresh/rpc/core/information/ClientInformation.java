package com.saucesubfresh.rpc.core.information;

import lombok.Getter;

/**
 * @author: 李俊平
 * @Date: 2021-10-30 13:43
 */
@Getter
public class ClientInformation {
    /**
     * The client id string pattern
     */
    private static final String SERVER_ID_PATTERN = "%s::%d";
    /**
     * Server address
     */
    private final String address;
    /**
     * Server port
     */
    private final int port;

    private ClientInformation(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Get formatted serverId
     *
     * @return The current server id
     */
    public String getServerId() {
        return String.format(SERVER_ID_PATTERN, this.address, this.port);
    }

    /**
     * Get {@link ClientInformation} instance
     *
     * @param address server address
     * @param port    server port
     * @return The {@link ClientInformation} instance
     */
    public static ClientInformation valueOf(String address, int port) {
        return new ClientInformation(address, port);
    }
}
