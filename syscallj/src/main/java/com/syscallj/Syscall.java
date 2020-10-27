package com.syscallj;

import com.syscallj.models.IoUringParams;

public final class Syscall {
    /**
     * Syscall 0, read a file
     * fd: file descriptor to use
     * buffer: the buffer to read into
     * size: amount of bytes to read
     * returns
     * long: amount of bytes read
     */
    public static long read(long fd, byte[] buffer, int size) {
        return Bridge.read(fd, buffer, size);
    }

    /**
     * Syscall 1, write to a file
     * fd: file descriptor to use
     * buffer: the buffer to use for writing
     * size: amount of bytes to write from the buffer
     * returns
     * long: amount of bytes written
     */
    public static long write(long fd, byte[] buffer, int size) {
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

    /**
     * Syscall 9, memory map
     * params:
     * addr: start address hint (0 to let the kernel decide)
     * len: amount of bytes to reserve
     * prot: protection settings for memory to allocate
     * flags: flags to pass, is this private or shared memory
     * fd: file descriptor to initialize the data from, use -1 with flag SHARED | ANONYMOUS to skip reading from a file
     * off: offset in the file to start at
     * returns:
     * long: a pointer to the address allocated or an error
     */
    public static long mmap(long addr, long len, long prot, long flags, long fd, long off) {
        return Bridge.mmap(addr, len, prot, flags, fd, off);
    }

    /**
     * Syscall 10, memory protect
     * params:
     * addr: the address to change
     * len: the amount of bytes to change
     * prot: the new protection settings
     * returns:
     * long: mprotect result code
     */
    public static long mprotect(long addr, long len, long prot) {
        return Bridge.mprotect(addr, len, prot);
    }

    /**
     * Syscall 11, memory unmap
     * params:
     * addr: the address to free
     * len: the amount of bytes to free
     * returns:
     * long: unmap result code
     */
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

    /**
     * Syscall 425, io_uring_setup
     * entries:
     * params:
     * returns:
     * long: file descriptor on success, <0 on error
     */
    public static long io_uring_setup(int entries, IoUringParams params)
    {
        return Bridge.io_uring_setup(entries, params);
    }

    /**
     * Syscall 426, io_uring_enter
     * params:
     * fd: the file descriptor
     * to_submit:
     * min_complete:
     * flags:
     * sig:
     * returns:
     * long:
     */
    public static long io_uring_enter(long fd, int to_submit, int min_complete, int flags, int[] sig)
    {
        return Bridge.io_uring_enter(fd, to_submit, min_complete, flags, sig);
    }

    /**
     * Syscall 427, io_uring_register
     * params:
     * fd: the file descriptor
     * opcode: the opcode to run?
     * arg: arguments to pass to the command
     * nr_args: number of arguments
     * returns:
     * long: io uring register result code
     */
    public static long io_uring_register(long fd, int opcode, byte[] arg, int nr_args)
    {
        return Bridge.io_uring_register(fd, opcode, arg, nr_args);
    }
}
