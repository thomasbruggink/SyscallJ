package com.syscallj.app;

import com.google.common.base.Charsets;
import com.syscallj.*;
import com.syscallj.models.IoUringParams;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static java.lang.System.out;

public class App {
    static String workDir = System.getProperty("user.dir");

    static void memset(long addr, long size, int val) {
        var unsafe = getUnsafe();
        unsafe.setMemory(addr, size, (byte)val);
    }

    static long malloc(long size) {
        return Syscall.mmap(0, size, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
    }

    static void free(long addr, long size) {
        Syscall.munmap(addr, size);
    }

    static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    static void read() {
        var fd = Syscall.open("/proc/version", FileFlags.NONE, FileModes.READ, FilePermissions.NONE);
        if (fd <= 0) {
            out.println("Unable to open file " + SyscallError.valueOf(fd));
            return;
        }
        var buffer = new byte[2000];
        var result = Syscall.read(fd, buffer, buffer.length);
        if (result < 0) {
            out.println("Could not read from file");
            return;
        }
        Syscall.close(fd);
        out.printf("Result: %d, %s\n", result, new String(buffer, 0, (int)result, Charsets.UTF_8));
    }

    static void write() {
        var fd = Syscall.open(workDir + "/test", FileFlags.CREATE.getValue(), FileModes.WRITE.getValue(),
                (short) (FilePermissions.WGRP.getValue() |
                        FilePermissions.RGRP.getValue() |
                        FilePermissions.WUSR.getValue() |
                        FilePermissions.RUSR.getValue()));
        if (fd <= 0) {
            out.println("Unable to open file: " + SyscallError.valueOf(fd));
            return;
        }
        var content = "Hello World!".getBytes();
        var writeResult = Syscall.write(fd, content, content.length);
        if (writeResult < 0) {
            out.println("Could not write to file " + SyscallError.valueOf(writeResult));
            return;
        }
        Syscall.close(fd);
        out.println(writeResult);
    }

    static void readKvmVersion() {
        var fd = Syscall.open("/dev/kvm", FileFlags.NONE, FileModes.READ, FilePermissions.NONE);
        if (fd <= 0) {
            out.println("Unable to open KVM: " + SyscallError.valueOf(fd));
            return;
        }

        var KVM_GET_API_VERSION = 44544L; // see linux/kvm.h

        var version = Syscall.ioctl(fd, KVM_GET_API_VERSION, null);
        out.println("KVM_GET_API_VERSION: " + version);

        var close = Syscall.close(fd);
        if (close < 0) {
            out.println("Unable to close KVM: " + SyscallError.valueOf(close));
        }
    }

    static void readKvmOptions() {
        var fd = Syscall.open("/dev/kvm", FileModes.READ);
        if (fd <= 0) {
            out.println("Unable to open KVM: " + SyscallError.valueOf(fd));
            return;
        }

        var KVM_GET_MSR_FEATURE_INDEX_LIST = 3221532170L; // see linux/kvm.h
        var KVMGET_MSRS = 3221794440L;
        var size = 3;

        var kvmMsrList = new KvmMsrList(size);
        var result = Syscall.ioctl(fd, KVM_GET_MSR_FEATURE_INDEX_LIST, kvmMsrList);
        out.println("KVM_GET_MSR_FEATURE_INDEX_LIST: " + result);
        for (var id : kvmMsrList.indices) {
            out.printf("0x%x%n", id);
        }

        var kvmMsrs = new KvmMsrs(size);
        for (var i = 0; i < size; i++) {
            kvmMsrs.kvmMsrs[i].index = kvmMsrList.indices[i];
        }
        result = Syscall.ioctl(fd, KVMGET_MSRS, kvmMsrs);
        out.println("KVMGET_MSRS: " + result);
        for (var msr : kvmMsrs.kvmMsrs) {
            out.printf("Index: 0x%x, Data 0x%x%n", msr.index, msr.data);
        }

        var close = Syscall.close(fd);
        if (close < 0) {
            out.println("Unable to close KVM: " + SyscallError.valueOf(close));
        }
    }

    static void readTerminalSettings() {
        var tcgets = 0x5401;
        var args = new TCGet();
        var result = Syscall.ioctl(0, tcgets, args);
        if (result < 0) {
            out.println("Unable to make ioctl call: " + SyscallError.valueOf(result));
            return;
        }
        out.println("CFlag: " + args.cflag);
    }

    static void memoryMapping() {
        var devZero = Syscall.open("/dev/zero", FileModes.READ_WRITE);
        if (devZero < 0) {
            out.println("Unable to open /dev/zero: " + SyscallError.valueOf(devZero));
            return;
        }
        var length = 1024;
        var addr = Syscall.mmap(0, length, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.PRIVATE.getValue(), devZero, 0);
        if (addr < 0) {
            out.println("Unable to make mmap call: " + SyscallError.valueOf(addr));
            return;
        }
        out.printf("addr: 0x%x\n", addr);
        var result = Syscall.munmap(addr, length);
        if (result < 0) {
            out.println("Unable to make munmap call: " + SyscallError.valueOf(result));
            return;
        }
        result = Syscall.close(devZero);
        if (result < 0) {
            out.println("Unable to close /dev/zero: " + SyscallError.valueOf(result));
        }
    }

    static void memoryMappingNoFd() {
        var length = 1024;
        var addr = Syscall.mmap(0, length, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
        if (addr < 0) {
            out.println("Unable to make mmap call: " + SyscallError.valueOf(addr));
            return;
        }
        out.printf("addr: 0x%x\n", addr);
        var result = Syscall.munmap(addr, length);
        if (result < 0) {
            out.println("Unable to make munmap call: " + SyscallError.valueOf(result));
        }
    }

    static void unsafeMemory() {
        var unsafe = getUnsafe();
        var size = 1024;
        var addr = Syscall.mmap(0, size, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
        unsafe.setMemory(addr, 20L, (byte)1);

        out.printf("Byte: %d\n", unsafe.getByte(addr));
        Syscall.munmap(addr, size);
    }

    static void readIOUring() {
        int size = 1024;
        int qd = 64;
        var addr = malloc(size);
        memset(addr, size, 0);
        var ioUringParams = new IoUringParams();
        ioUringParams.flags = 0;
        var fd = Syscall.io_uring_setup(qd, ioUringParams);
        if(fd < 0) {
            out.printf("Unable to initialize IO_URING_SETUP: %d\n", fd);
            return;
        }
        out.printf("IO_URING_SETUP: %d\n", fd);
        free(addr, size);
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", System.getProperty("java.library.path") + ":" + workDir + "/native/build/lib/main/debug");
        out.println(System.getProperty("java.library.path"));

//        read();
//        write();
//        readTerminalSettings();
//        readKvmVersion();
//        readKvmOptions();
//        memoryMapping();
//        memoryMappingNoFd();
//        unsafeMemory();
        readIOUring();
    }
}
