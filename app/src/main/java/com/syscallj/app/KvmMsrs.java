package com.syscallj.app;

import com.syscallj.SyscallObject;

public class KvmMsrs extends SyscallObject {
    public int nmsrs;
    public int pad;

    public KvmMsr[] kvmMsrs;

    public KvmMsrs(int size) {
        nmsrs = size;
        pad = 0;
        kvmMsrs = new KvmMsr[size];
        for(var i = 0; i < size; i++) {
            kvmMsrs[i] = new KvmMsr();
        }
    }
}
