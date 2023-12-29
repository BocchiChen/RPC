package com.cxy.rpc.core.enums;

import lombok.Getter;

@Getter
public enum MessageStatus {
    SUCCESS((byte) 0),

    /**
     * 失败
     */
    FAIL((byte) 1);

    private final byte code;

    MessageStatus(byte code) {
        this.code = code;
    }

    public static boolean isSuccess(byte code) {
        return MessageStatus.SUCCESS.code == code;
    }
}
