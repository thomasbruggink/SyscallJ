package com.syscallj.models;

import com.syscallj.SyscallObject;

public class CompatStat extends SyscallObject {
    public long dev;
    public long ino;
    public long nlink;
    public int mode;
    public int uid;
    public int gid;
    public int pad0;
    public long rdev;
    public long size;
    public long blksize;
    public long blocks;
    public TimeSpec atime = new TimeSpec();
    public TimeSpec mtime = new TimeSpec();
    public TimeSpec ctime = new TimeSpec();
    public long unused4;
    public long unused5;
    public long unused6;
}
