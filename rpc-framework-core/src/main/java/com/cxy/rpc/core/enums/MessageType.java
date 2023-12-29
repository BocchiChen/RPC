package com.cxy.rpc.core.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    REQUEST((byte) 0),

    RESPONSE((byte) 1),

    HEARTBEAT_REQUEST((byte) 2),

    HEARTBEAT_RESPONSE((byte) 3);

    private final byte type;

    MessageType(byte type) {
        this.type = type;
    }

    public static MessageType parseByType(byte type) throws IllegalArgumentException {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getType() == type) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(String.format("The message type %s is illegal.", type));
    }
}
