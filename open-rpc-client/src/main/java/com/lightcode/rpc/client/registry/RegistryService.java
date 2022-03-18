package com.lightcode.rpc.client.registry;

/**
 * @author lijunping on 2022/1/24
 */
public interface RegistryService {

    /**
     * Register client to server
     *
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    boolean register(String serverAddress, int serverPort);

    /**
     * deRegister client
     * @param clientAddress - The client address
     * @param clientPort - The client port
     */
    boolean deRegister(String clientAddress, int clientPort);
}
