package com.syscallj.models;

import com.syscallj.SyscallObject;

public class IoVec extends SyscallObject {
    public long base; // Pointer to data
    public long len; // Length of data
}
