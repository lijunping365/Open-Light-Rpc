package com.saucesubfresh.rpc.core.enums;

/**
 * @author lijunping on 2022/2/16
 */
public enum PacketType {

    REGISTER(0, true), DEREGISTER(1, true),

    MESSAGE(1), PING(2), PONG(3),

    ;

    public static final PacketType[] VALUES = values();
    private final int value;
    private final boolean inner;

    PacketType(int value) {
        this(value, false);
    }

    PacketType(int value, boolean inner) {
        this.value = value;
        this.inner = inner;
    }

    public int getValue() {
        return value;
    }

    public static PacketType valueOf(int value) {
        for (PacketType type : VALUES) {
            if (type.getValue() == value && !type.inner) {
                return type;
            }
        }
        throw new IllegalStateException();
    }

    public static PacketType valueOfInner(int value) {
        for (PacketType type : VALUES) {
            if (type.getValue() == value && type.inner) {
                return type;
            }
        }
        throw new IllegalArgumentException("Can't parse " + value);
    }
}
