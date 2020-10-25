package com.syscallj;

public enum MemoryFlags {
    NONE(0x0),
    SHARED(0x1), // Share changes
    PRIVATE(0x3), // share + validate extension flags
    SHARED_VALIDATE(0x2), // Changes are private
    ANONYMOUS(0x20), // Dont use a file
    FIXED(0x100); // Interpret addr exactly

    private short value;

    MemoryFlags(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }
}
