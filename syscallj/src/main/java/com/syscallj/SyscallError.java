package com.syscallj;

import java.util.HashMap;
import java.util.Map;

public enum SyscallError {
    EPERM(1, "Operation not permitted"),
    ENOENT(2, "No such file or directory"),
    ESRCH(3, "No such process"),
    EINTR(4, "Interrupted system call"),
    EIO(5, "I/O error"),
    ENXIO(6, "No such device or address"),
    E2BIG(7, "Argument list too long"),
    ENOEXEC(8, "Exec format error"),
    EBADF(9, "Bad file number"),
    ECHILD(10, "No child processes"),
    EAGAIN(11, "Try again"),
    ENOMEM(12, "Out of memory"),
    EACCES(13, "Permission denied"),
    EFAULT(14, "Bad address"),
    ENOTBLK(15, "Block device required"),
    EBUSY(16, "Device or resource busy"),
    EEXIST(17, "File exists"),
    EXDEV(18, "Cross-device link"),
    ENODEV(19, "No such device"),
    ENOTDIR(20, "Not a directory"),
    EISDIR(21, "Is a directory"),
    EINVAL(22, "Invalid argument"),
    ENFILE(23, "File table overflow"),
    EMFILE(24, "Too many open files"),
    ENOTTY(25, "Not a typewriter"),
    ETXTBSY(26, "Text file busy"),
    EFBIG(27, "File too large"),
    ENOSPC(28, "No space left on device"),
    ESPIPE(29, "Illegal seek"),
    EROFS(30, "Read-only file system"),
    EMLINK(31, "Too many links"),
    EPIPE(32, "Broken pipe"),
    EDOM(33, "Math argument out of domain of func"),
    ERANGE(34, "Math result not representable");

    private short value;
    private String message;
    private static Map<Short, SyscallError> map = new HashMap<>();

    static {
        for (SyscallError error : SyscallError.values()) {
            map.put(error.value, error);
        }
    }

    private SyscallError(int value, String message) {
        this.value = (short)value;
        this.message = message;
    }

    public static SyscallError valueOf(long code) {
        return map.get((short)Math.abs(code));
    }
    
    public short getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return value + ", " + message;
    }
}
