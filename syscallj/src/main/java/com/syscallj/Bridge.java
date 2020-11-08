package com.syscallj;

class Bridge {
    // Syscalls
    static native long read(long fd, byte[] buffer, int size);
    static native long write(long fd, byte[] buffer, int size);
    static native long open(String fileName, int flags, short mode);
    static native long close(long fd);
    static native long fstat(long fd, Object compatStat);
    static native long mmap(long addr, long len, long prot, long flags, long fd, long off);
    static native long mprotect(long addr, long len, long prot);
    static native long munmap(long addr, long len); 
    static native long ioctl(long fd, long command, Object argument);
    static native long io_uring_setup(int entries, Object params);
    static native long io_uring_enter(long fd, int to_submit, int min_complete, int flags, Object sig);
    static native long io_uring_register(long fd, int opcode, byte[] arg, int nr_args);

    // Memory helpers
    static native void read_address_as(long addr, Object dest, boolean withBarrier);
    static native void write_to_address(Object dest, long addr, boolean withBarrier);

    static {
        System.loadLibrary("native");
    }
}
