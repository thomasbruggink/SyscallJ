package com.syscallj.app;

import com.syscallj.SyscallObject;

public class IoUringCqe extends SyscallObject {
    long userData;
    int res;
    int flags;
}
