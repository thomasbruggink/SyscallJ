package com.syscallj;

public enum FilePermissions {
    NONE(0),
    RWXU(0700),
    RUSR(0400),
    WUSR(0200),
    XUSR(0100),
    RWXG(0070),
    RGRP(0040),
    WGRP(0020),
    XGRP(0010),
    RWXO(0007),
    ROTH(0004),
    WOTH(0002),
    XOTH(0001);

    private final short value;

    FilePermissions(int value) {
        this.value = (short)value;
    }

    public short getValue() {
        return value;
    }
}
