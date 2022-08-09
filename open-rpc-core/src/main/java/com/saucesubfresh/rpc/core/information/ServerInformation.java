package com.saucesubfresh.rpc.core.information;

import com.saucesubfresh.rpc.core.constants.CommonConstant;
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
public class ServerInformation {
    /**
     * server address
     */
    private final String address;
    /**
     * server port
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

    public ServerInformation(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Get formatted clientId
     *
     * @return The current client id
     */
    public String getServerId() {
        return String.format(CommonConstant.ADDRESS_PATTERN, this.address, this.port);
    }

    /**
     * Get new {@link ServerInformation} instance
     *
     * @param address client address
     * @param port    client port
     * @return {@link ServerInformation} instance
     */
    public static ServerInformation valueOf(String address, int port) {
        return new ServerInformation(address, port);
    }
}
