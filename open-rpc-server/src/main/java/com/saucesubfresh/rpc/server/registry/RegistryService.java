package com.saucesubfresh.rpc.server.registry;

/**
 * @author lijunping on 2022/1/24
 */
public interface RegistryService {

    /**
     * Register server
     *
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    void register(String serverAddress, int serverPort);

    /**
     * deRegister server
     * @param serverAddress The server address
     * @param serverPort    The server port
     */
    void deRegister(String serverAddress, int serverPort);
}
