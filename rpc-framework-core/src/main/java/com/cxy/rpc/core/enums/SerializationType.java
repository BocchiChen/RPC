package com.cxy.rpc.core.enums;

import lombok.Getter;

@Getter
public enum SerializationType {
    JDK((byte) 0),

    JSON((byte) 1),

    HESSIAN((byte) 2),

    KRYO((byte) 3),

    PROTOSTUFF((byte) 4);

    private final byte type;

    SerializationType(byte type) {
        this.type = type;
    }

    public static SerializationType parseByName(String serializeName) {
        for (SerializationType serializationType : SerializationType.values()) {
            if (serializationType.name().equalsIgnoreCase(serializeName)) {
                return serializationType;
            }
        }
        return HESSIAN;
    }

    public static SerializationType parseByType(byte type) {
        for (SerializationType serializationType : SerializationType.values()) {
            if (serializationType.getType() == type) {
                return serializationType;
            }
        }
        return HESSIAN;
    }
}
