package com.syscallj.app;

import com.syscallj.SyscallObject;

public class IoSqringOffsets extends SyscallObject {
    int head; // offset of the ring head
    int tail; // offset of the tail head
    int ringMask; // ring mask value
    int ringEntries; // entries in the ring
    int flags; // ring flags
    int droppped; // number sqes not submitted
    int array; // sqe index array
    int resv1; // reserved
    long resv2; // reserved
}

