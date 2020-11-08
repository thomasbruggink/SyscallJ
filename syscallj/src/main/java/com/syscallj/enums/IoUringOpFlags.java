package com.syscallj.enums;

import java.util.HashMap;
import java.util.Map;

public enum IoUringOpFlags {
    NOP(0),
    READV(1),
    WRITEV(2),
    FSYNC(3),
    READ_FIXED(4),
    WRITE_FIXED(5),
    POLL_ADD(6),
    POLL_REMOVE(7),
    SYNC_FILE_RANGE(8),
    SENDMSG(9),
    RECVMSG(10),
    TIMEOUT(11);

    private final byte value;
    private static final Map<Byte, IoUringOpFlags> map = new HashMap<>();

    static {
        for (IoUringOpFlags error : IoUringOpFlags.values()) {
            map.put(error.value, error);
        }
    }

    IoUringOpFlags(int value) {
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    public static IoUringOpFlags valueOf(byte value) {
        return map.get((byte)Math.abs(value));
    }
}
