package com.syscallj.app;

import com.syscallj.SyscallObject;

public class IoUringSqe extends SyscallObject {
    byte opcode;
    byte flags;
    short ioprio;
    int fd;
    long off;
    long addr;
    int len;
    int rwFlags;
    int fsyncFlags;
    short pollEvents;
    int syncRangeFlags;
    int msgFlags;
    long userData;
    short bufIndex;
    long[] pad2 = new long[3];
}
