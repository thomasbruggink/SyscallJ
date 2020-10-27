package com.syscallj.models;

import com.syscallj.SyscallObject;

public class IoUringParams extends SyscallObject {
    public int sqEntries;
    public int cqEntries;
    public int flags;
    public int sqThreadCpu;
    public int sqThreadIdle;
    public int[] resv = new int[5];
    public IoSqringOffsets sqOff = new IoSqringOffsets();
    public IoCqringOffsets cqOff = new IoCqringOffsets();
}
