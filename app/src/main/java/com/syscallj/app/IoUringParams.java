package com.syscallj.app;

import com.syscallj.SyscallObject;

public class IoUringParams extends SyscallObject {
    int sqEntries;
    int cqEntries;
    int flags;
    int sqThreadCpu;
    int sqThreadIdle;
    int[] resv = new int[5];
    IoSqringOffsets sqOff;
    IoCqringOffsets cqOff;
}
