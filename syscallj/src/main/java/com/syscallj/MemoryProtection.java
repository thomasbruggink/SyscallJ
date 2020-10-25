package com.syscallj;

public enum MemoryProtection {
    READ(0x1), // page can be read
    WRITE(0x2), // page can be written
    EXEC(0x3), // page can be executed
    SEM(0x8), // page may be used for atomic ops
    NONE(0x0), // page can not be accessed
    GROWSDOWN(0x01000000), // mprotect flag: extend change to start of growsdown vma
    GROWSUP(0x02000000); // mprotect flag: extend change to end of growsup vma

    private short value;

    MemoryProtection(int value) {
        this.value = (short) value;
    }

    public short getValue() {
        return value;
    }
}
