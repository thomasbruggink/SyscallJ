package com.syscallj.enums;

public enum MemoryFlags {
    NONE(0x0),
    SHARED(0x1), // Share changes
    PRIVATE(0x3), // share + validate extension flags
    SHARED_VALIDATE(0x2), // Changes are private
    ANONYMOUS(0x20), // Dont use a file
    FIXED(0x100), // Interpret addr exactly
    POPULATE(0x8000), // populate (prefault) pagetables
    NONBLOCK(0x010000),	// do not block on IO
    STACK(0x020000),	// give out an address that is best suited for process/thread stacks
    HUGETLB(0x040000),	// create a huge page mapping
    SYNC(0x080000), // perform synchronous page faults for the mapping
    FIXED_NOREPLACE(0x100000),	// MAP_FIXED which doesn't unmap underlying mapping
    MAP_UNINITIALIZED(0x4000000); //For anonymous mmap, memory could be uninitialized

    private final long value;

    MemoryFlags(long value) {
        this.value = (long) value;
    }

    public long getValue() {
        return value;
    }
}
