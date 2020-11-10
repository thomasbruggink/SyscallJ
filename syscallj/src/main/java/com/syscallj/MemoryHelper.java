package com.syscallj;

import com.syscallj.enums.MemoryFlags;
import com.syscallj.enums.MemoryProtection;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class MemoryHelper {
    public static long malloc(long size) {
        return Syscall.mmap(0, size, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
    }

    public static void free(long addr, long size) {
        Syscall.munmap(addr, size);
    }

    public static void memset(long addr, long size, int val) {
        var unsafe = Unsafe.getUnsafe();
        unsafe.setMemory(addr, size, (byte) val);
    }

    public static void readAddressAs(long addr, SyscallObject args) {
        Bridge.read_address_as(addr, args);
    }

    public static void writeToAddress(SyscallObject args, long addr) {
        Bridge.write_to_address(args, addr);
    }

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
