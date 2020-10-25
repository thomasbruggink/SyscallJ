#pragma once

#include <stddef.h>
#include <linux/types.h>

namespace syscallj
{
    class Syscall
    {
    public:
        /**
         * Syscall 0, read a file
         * fd: file destcriptor to use
         * buffer: buffer to read into
         * size: the amount of bytes to read into the buffer
         * returns
         * int bytes read
         */
        static long read(long fd, const char *buffer, size_t size);
        /**
         * Syscall 1, write to a file
         * fd: file descriptor to use
         * buffer: buffer to write
         * size: the amount of bytes to write from the buffer
         * returns
         * int bytes written
         */
        static long write(long fd, const char *buffer, size_t size);
        /**
         * Syscall 2, open a file
         * params:
         * fileName: the path to load
         * flags: the flags to use
         * mode: the filemode to open the file in
         * returns:
         * long: open result code
         */
        static long open(const char *fileName, int flags, unsigned short mode);
        /**
         * Syscall 3, close a file
         * params:
         * fd: the file descriptor to close
         * returns:
         * long: close result code
         */
        static long close(long fd);
        /**
         * Syscall 9, memory map
         * params:
         * addr: start address hint (null to let the kernel decide)
         * len: amount of bytes to reserve
         * prot: 
         * flags: 
         * fd: 
         * off: 
         * returns:
         * long: 
         */
        static long mmap(unsigned long addr, unsigned long len, unsigned long prot, unsigned long long flags, unsigned long fd, unsigned long off);
        /**
         * Syscall 10, memory protect
         * params:
         * addr: 
         * len: 
         * prot: 
         * returns:
         * long: mprotect result code
         */
        static long mprotect(unsigned long addr, unsigned long len, unsigned long prot);
        /**
         * Syscall 11, memory unmap
         * params:
         * addr: 
         * len: 
         * returns:
         * long: unmap result code
         */
        static long munmap(unsigned long addr, unsigned long len);        
        /**
         * Syscall 16, ioctl
         * params:
         * fd: the file descriptor
         * cmd: the command to run
         * arg: arguments to pass to the command
         * returns:
         * long: ioctl result code
         */
        static long ioctl(long fd, long cmd, long arg);
        /**
         * Syscall 425, io_uring_setup
         * entries: 
         * params: 
         * returns:
         * long: file descriptor on success, <0 on error
         */
        static long io_uring_setup(unsigned int entries, struct io_uring_params *params);
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
        static long io_uring_enter(long fd, unsigned int to_submit, unsigned int min_complete, unsigned int flags, int *sig);
        /**
         * Syscall 427, io_uring_register
         * params:
         * fd: the file descriptor
         * opcode: the opcode to run?
         * arg: arguments to pass to the command
         * nr_args: number of arguments
         * returns:
         * long: ioctl result code
         */
        static long io_uring_register(long fd, unsigned int opcode, void *arg, unsigned int nr_args);
    private:
        static long syscall(int c...);
    };
} // namespace syscallj