package com.syscallj.enums;

public enum IoUringOffsets {
    SQ_RING(0),
    CQ_RING(0x8000000),
    SQES(0x10000000);

    private final long value;

    IoUringOffsets(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
