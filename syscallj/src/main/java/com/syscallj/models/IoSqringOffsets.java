package com.syscallj.models;

import com.syscallj.SyscallObject;
import com.syscallj.MemoryHelper;

public class IoSqringOffsets extends SyscallObject {
    public int head; // offset of the ring head
    public int tail; // offset of the tail head
    public int ringMask; // ring mask value
    public int ringEntries; // entries in the ring
    public int flags; // ring flags
    public int droppped; // number sqes not submitted
    public int array; // sqe index array
    public int resv1; // reserved
    public long resv2; // reserved

//    public IoUringSqe getSqeAtIndex(int index) {
//        var unsafe = MemoryHelper.getUnsafe();
//        var offset = unsafe.getLong(array) + (index*8);
//        var result = new IoUringSqe();
//        MemoryHelper.readAddressAs(offset, result);
//        return result;
//    }
//
//    public void setSqeAtIndex(int index, IoUringSqe obj) {
//        var unsafe = MemoryHelper.getUnsafe();
//        var offset = unsafe.getAddress(array) + (index*8);
//        MemoryHelper.writeToAddress(obj, offset);
//    }
}

