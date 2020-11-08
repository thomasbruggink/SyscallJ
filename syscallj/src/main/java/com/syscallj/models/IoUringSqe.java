package com.syscallj.models;

import com.syscallj.*;
import com.syscallj.enums.IoUringOpFlags;

/**
 * IO submission data structure (Submission Queue Entry)
 */
public class IoUringSqe extends SyscallObject {
    public byte opcode; // Operation to execute
    public byte flags; // IOSQE_ flags
    public short ioprio; // ioprio for the request
    public int fd; // File descriptor to operate on
    public long off; // Offset in the file
    public long addr; // Address to write to this must be an io_vec
    public int len; // Amount of bytes to read
    /**
     * Union of one of the following types:
     * int rwFlags;
     * int fsyncFlags;
     * short pollEvents;
     * int syncRangeFlags;
     * int msgFlags;
     * int timeoutFlags;
     */
    public int union1;
    public long userData; // Data to pass through io uring, this data is not used in the kernel
    public short bufIndex;
    /**
     * Padding to extend bufIndex to 24 bytes
     */
    public short[] pad1 = new short[3];
    public long[] pad2 = new long[2];

    public void setOpcode(IoUringOpFlags op) {
        opcode = op.getValue();
    }

    public IoUringOpFlags getOpCode() {
        return IoUringOpFlags.valueOf(opcode);
    }
}
