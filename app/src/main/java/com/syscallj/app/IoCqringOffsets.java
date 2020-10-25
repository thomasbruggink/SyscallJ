package com.syscallj.app;

import com.syscallj.SyscallObject;

public class IoCqringOffsets extends SyscallObject {
    int head; // offset of the ring head
    int tail; // offset of the tail head
    int ringMask; // ring mask value
    int ringEntries; // entries in the ring
    int overflow;
    int cqes;
    long[] resv = new long[2]; // reserved
}
