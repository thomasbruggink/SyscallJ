package com.syscallj;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class MemoryHelper {
    public static void readAddressAs(long addr, SyscallObject args) {
        readAddressAs(addr, args, false);
    }

    public static void readAddressAs(long addr, SyscallObject args, boolean withBarrier) {
        Bridge.read_address_as(addr, args, withBarrier);
    }

    public static void writeToAddress(SyscallObject args, long addr) {
        Bridge.write_to_address(args, addr, false);
    }

    public static void writeToAddress(SyscallObject args, long addr, boolean withBarrier) {
        Bridge.write_to_address(args, addr, withBarrier);
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
