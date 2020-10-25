package com.syscallj.app;

import com.syscallj.SyscallObject;

public class KvmMsr extends SyscallObject {
    public int index;
    public int reserved;
    public long data;
}
