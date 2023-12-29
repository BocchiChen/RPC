package com.cxy.rpc.core.constant;

import java.util.concurrent.atomic.AtomicInteger;

public class ProtocolConstants {
    private static final AtomicInteger ai = new AtomicInteger();

    public static final byte[] MAGIC_NUM = new byte[]{(byte) 'x', (byte) 'r', (byte) 'p', (byte) 'c'};

    public static final byte VERSION = 1;

    public static final String PING = "ping";

    public static final String PONG = "pong";

    public static int getSequenceId() {
        return ai.getAndIncrement();
    }
}
