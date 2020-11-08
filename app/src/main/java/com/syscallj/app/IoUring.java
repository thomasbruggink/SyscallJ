package com.syscallj.app;

import com.syscallj.models.IoUringParams;

public class IoUring {
    public IoUring(long fd, IoUringParams params) {

    }

    private class IoUringSq {
        long head;
        long tail;
        long ringMask;
        long ringEntries;
        long flags;
        long dropped;
        long array;

    }
}
