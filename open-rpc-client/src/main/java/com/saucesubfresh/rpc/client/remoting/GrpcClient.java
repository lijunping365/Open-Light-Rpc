package com.saucesubfresh.rpc.client.remoting;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author lijunping on 2022/8/23
 */
public class GrpcClient implements RpcClient{

    public ManagedChannel connect(String address, int port){
        return ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
    }
}
