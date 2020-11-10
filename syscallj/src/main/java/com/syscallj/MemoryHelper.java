package com.syscallj;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class MemoryHelper {
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
