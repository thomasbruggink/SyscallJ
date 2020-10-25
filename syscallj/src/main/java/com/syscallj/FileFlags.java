package com.syscallj;

public enum FileFlags {
    NONE(0),
    APPEND(02000),
    ASYNC(020000),
    CLOEXEC(02000000),
    CREATE(0100),
    DIRECT(040000),
    DIRECTORY(0200000),
    DSYNC(010000),
    EXCL(0200),
    LARGEFILE(0100000),
    NOATIME(01000000),
    NOCTTY(0400),
    NOFOLLOW(0400000),
    NONBLOCK(04000),
    NDELAY(04000),
    PATH(010000000),
    SYNC(04000000|010000),
    TMPFILE(020000000),
    TRUNC(01000);

    private short value;

    private FileFlags(int value) {
        this.value = (short)value;
    }

    public short getValue() {
        return value;
    }
}
