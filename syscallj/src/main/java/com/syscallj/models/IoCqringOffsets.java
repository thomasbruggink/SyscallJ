package com.syscallj.models;

import com.syscallj.SyscallObject;

public class IoCqringOffsets extends SyscallObject {
    public int head; // offset of the ring head
    public int tail; // offset of the tail head
    public int ringMask; // ring mask value
    public int ringEntries; // entries in the ring
    public int overflow;
    public int cqes; // pointer to a list of cq entries
    public long[] resv = new long[2]; // reserved
}
