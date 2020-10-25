package com.syscallj.app;

import com.syscallj.SyscallObject;

public class KvmMsrList extends SyscallObject {
    public int nmsrs;
    public int[] indices;

    public KvmMsrList(int size) {
        nmsrs = size;
        indices = new int[size];
    }
}
