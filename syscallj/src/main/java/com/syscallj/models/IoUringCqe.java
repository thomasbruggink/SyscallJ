package com.syscallj.models;

import com.syscallj.SyscallObject;

/**
 * IO communication data structure (Communication Queue Entry)
 */
public class IoUringCqe extends SyscallObject {
    public long userData;
    public int res;
    public int flags;
}
