package com.syscallj.models;

import com.syscallj.SyscallObject;

public class IoSqringOffsets extends SyscallObject {
    public int head; // offset of the ring head
    public int tail; // offset of the tail head
    public int ringMask; // ring mask value
    public int ringEntries; // entries in the ring
    public int flags; // ring flags
    public int droppped; // number sqes not submitted
    public int array; // sqe index array
    public int resv1; // reserved
    public long resv2; // reserved
}

