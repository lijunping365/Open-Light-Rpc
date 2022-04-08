package com.saucesubfresh.rpc.core.exception;

import lombok.Data;

/**
 * @author lijunping on 2022/1/21
 */
@Data
public class RpcException extends RuntimeException{

    private int code;

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;

    public RpcException(String msg){
        super(msg);
    }

    public RpcException(int code) {
        super();
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }
}
