#include "Syscall.h"
#include <cstdarg>

using namespace syscallj;

long Syscall::read(long fd, const char *buffer, size_t size)
{
    const int READ = 0;
    return syscall(3, READ, fd, buffer, size);
}

long Syscall::write(long fd, const char *buffer, size_t size)
{
    const int WRITE = 1;
    return syscall(3, WRITE, fd, buffer, size);
}

long Syscall::open(const char *fileName, int flags, unsigned short mode)
{
    const int OPEN = 2;
    return syscall(3, OPEN, fileName, flags, mode);
}

long Syscall::close(long fd)
{
    const int CLOSE = 3;
    return syscall(1, CLOSE, fd);
}

long Syscall::mmap(unsigned long addr, unsigned long len, unsigned long prot, unsigned long long flags, unsigned long fd, unsigned long off)
{
    const int MMAP = 9;
    return syscall(6, MMAP, addr, len, prot, flags, fd, off);
}

long Syscall::mprotect(unsigned long addr, unsigned long len, unsigned long prot)
{
    const int MPROTECT = 10;
    return syscall(3, MPROTECT, addr, len, prot);
}

long Syscall::munmap(unsigned long addr, unsigned long len)
{
    const int MUNMAP = 11;
    return syscall(2, MUNMAP, addr, len);
}

long Syscall::ioctl(long fd, long cmd, long arg)
{
    const int IOCTL = 16;
    return syscall(3, IOCTL, fd, cmd, arg);
}

long Syscall::io_uring_setup(unsigned int entries, struct io_uring_params *params)
{
    const int IO_URING_SETUP = 425;
    return syscall(2, IO_URING_SETUP, entries, params);
}
       
long Syscall::io_uring_enter(long fd, unsigned int to_submit, unsigned int min_complete, unsigned int flags, int *sig)
{
    const int IO_URING_ENTER = 426;
    return syscall(5, IO_URING_ENTER, fd, to_submit, min_complete, flags, sig);
}
       
long Syscall::io_uring_register(long fd, unsigned int opcode, void *arg, unsigned int nr_args)
{
    const int IO_URING_REGISTER = 427;
    return syscall(4, IO_URING_REGISTER, fd, opcode, arg, nr_args);
}

long Syscall::syscall(int c...)
{
    int i;
    va_list valist;
    va_start(valist, c);
    int size = sizeof(unsigned long long);
    unsigned long long data[c+1];
    for (i = 0; i < c+1; i++)
        data[i] = va_arg(valist, unsigned long long);
    long result;
    __asm__("mov rax, %[args]\t\n"
            "push [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov rdi, [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov rsi, [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov rdx, [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov r10, [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov r8, [rax]\t\n"
            "add rax, 0x8\t\n"
            "mov r9, [rax]\t\n"
            "pop rax\t\n"
            "syscall\t\n"
            "mov %[return_code], rax\t\n"
            : [ return_code ] "=r"(result)
            : [ args ] "r"(data));
    va_end(valist);
    return result;
}