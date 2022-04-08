package com.saucesubfresh.rpc.core.converter;


import com.saucesubfresh.rpc.core.Message;

/**
 * @author: 李俊平
 * @Date: 2021-10-30 13:42
 */
@FunctionalInterface
public interface MessageConverter {
    /**
     * Convert {@link Message} to return value type instance
     *
     * @param message The {@link Message} instance
     * @param <R>     The type want to convert
     * @return Converted instance
     */
    <R> R convert(Message message);
}
