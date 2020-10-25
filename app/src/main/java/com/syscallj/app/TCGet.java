package com.syscallj.app;

import com.syscallj.SyscallObject;

public class TCGet extends SyscallObject {
    public int iflag; // input mode flags
    public int oflag; // output mode flags
    public int cflag; // control mode flags
    public int lflag; // local mode flags
    public byte line; // line discipline
    public byte[] cc = new byte[19]; // control characters
}
