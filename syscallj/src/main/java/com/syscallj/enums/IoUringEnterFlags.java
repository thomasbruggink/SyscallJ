package com.syscallj.enums;

public enum IoUringEnterFlags {
    ENTER_GETEVENTS(1),
    ENTER_SQ_WAKEUP(1<<1);

    private final int value;

    IoUringEnterFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
