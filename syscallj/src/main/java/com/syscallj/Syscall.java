package com.syscallj;

public final class Syscall {
    /**
     * Syscall 0, read a file
     * fd: file descriptor to use
     * size: amount of bytes to read
     * returns
     * string the data read
     */
    public static String read(long fd, int size) {
        return Bridge.read(fd, size);
    }

    /**
     * Syscall 1, write to a file
     * fd: file descriptor to use
     * buffer: the buffer to use for writing
     * size: amount of bytes to write from the buffer
     * returns
     * long: amount of bytes written
     */
    public static long write(long fd, String buffer, int size) {
        return Bridge.write(fd, buffer, size);
    }

    /**
     * Syscall 2, open a file
     * params:
     * fileName: the path to load
     * flags: the flags to use when opening the file, see `man open(2)` for a description of the flags
     * mode: the filemode to open the file in
     * permissions: if the create flag is specified the permissions for the file
     * returns:
     * long: the file descriptor or if less than 0 an error
     * common errors:
     * -2: file not found
     * -5: I/O error
     * -13: permission denied
     * -20: not a directory
     * -21: is a directory
     * -24: too many open files
     */
    public static long open(String fileName, int flags, short mode, short permissions) {
        return Bridge.open(fileName, flags | mode, permissions);
    }

    /**
     * Syscall 2, open a file
     * See open(string, int, short, short) for documentation.
     */
    public static long open(String fileName, FileFlags flags, FileModes mode, FilePermissions permissions) {
        return Bridge.open(fileName, flags.getValue() | mode.getValue(), permissions.getValue());
    }

    /**
     * Syscall 2, open a file
     * See open(string, int, short, short) for documentation.
     */
    public static long open(String fileName, FileModes mode) {
        return Bridge.open(fileName, mode.getValue(), (short) 0);
    }

    /**
     * Syscall 3, close a file
     * params:
     * fd: the file descriptor to close
     * returns:
     * long: close result code
     */
    public static long close(long fd) {
        return Bridge.close(fd);
    }

    public static long mmap(long addr, long len, long prot, long flags, long fd, long off) {
        return Bridge.mmap(addr, len, prot, flags, fd, off);
    }

    public static long mprotect(long addr, long len, long prot) {
        return Bridge.mprotect(addr, len, prot);
    }

    public static long munmap(long addr, long len) {
        return Bridge.munmap(addr, len);
    }

    /**
     * Syscall 16, ioctl
     * params:
     * fd: the file descriptor to close
     * command: command to run
     * args: argument to pass with the call (depending on the ioctl call this object will be written to)
     * returns:
     * long: ioctl result code
     */
    public static long ioctl(long fd, long command, SyscallObject args) {
        return Bridge.ioctl(fd, command, args);
    }
}
