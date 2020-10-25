package com.syscallj;

public enum FileModes {
    READ(0),
    WRITE(1),
    READ_WRITE(2);

    private final short value;

    FileModes(int value) {
        this.value = (short)value;
    }

    public short getValue() {
        return value;
    }
}
