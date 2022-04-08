package com.saucesubfresh.rpc.core.information;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: 李俊平
 * @Date: 2021-10-30 13:42
 */
@ToString
@Getter
public class ClientInformation {
    /**
     * The client id string pattern
     */
    private static final String CLIENT_ID_PATTERN = "%s::%d";
    /**
     * client address
     */
    private final String address;
    /**
     * client port
     */
    private final int port;
    /**
     * first online time
     */
    @Setter
    private long onlineTime;
    /**
     * this client status
     */
    @Setter
    private ClientStatus status;
    /**
     * node init weight
     */
    @Setter
    private int weight = 1;

    public ClientInformation(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Get formatted clientId
     *
     * @return The current client id
     */
    public String getClientId() {
        return String.format(CLIENT_ID_PATTERN, this.address, this.port);
    }

    /**
     * Get new {@link ClientInformation} instance
     *
     * @param address client address
     * @param port    client port
     * @return {@link ClientInformation} instance
     */
    public static ClientInformation valueOf(String address, int port) {
        return new ClientInformation(address, port);
    }
}
