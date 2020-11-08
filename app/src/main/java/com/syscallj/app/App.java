package com.syscallj.app;

import com.syscallj.MemoryHelper;
import com.syscallj.Syscall;
import com.syscallj.SyscallError;
import com.syscallj.enums.*;
import com.syscallj.models.*;

import java.nio.charset.StandardCharsets;

import static java.lang.System.out;

public class App {
    static String workDir = System.getProperty("user.dir");

    static void memset(long addr, long size, int val) {
        var unsafe = MemoryHelper.getUnsafe();
        unsafe.setMemory(addr, size, (byte) val);
    }

    static long malloc(long size) {
        return Syscall.mmap(0, size, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
    }

    static void free(long addr, long size) {
        Syscall.munmap(addr, size);
    }

    static void read() {
        var fd = Syscall.open(workDir + "/README.md", FileFlags.NONE, FileModes.READ, FilePermissions.NONE);
        if (fd <= 0) {
            out.println("Unable to open file " + SyscallError.valueOf(fd));
            return;
        }
        var stat = new CompatStat();
        var fstatRes = Syscall.fstat(fd, stat);
        if (fstatRes < 0) {
            out.printf("Could not fstat file %d\n", fstatRes);
            return;
        }
        var buffer = new byte[(int) stat.size];
        var result = Syscall.read(fd, buffer, buffer.length);
        if (result < 0) {
            out.println("Could not read from file");
            return;
        }
        Syscall.close(fd);
        out.printf("Result: %d\n%s\n", result, new String(buffer, 0, (int) result, StandardCharsets.UTF_8));
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
        var unsafe = MemoryHelper.getUnsafe();
        var size = 1024;
        var addr = Syscall.mmap(0, size, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.ANONYMOUS.getValue(), -1, 0);
        unsafe.setMemory(addr, 20L, (byte) 1);

        out.printf("Byte: %d\n", unsafe.getByte(addr));
        Syscall.munmap(addr, size);
    }

    static void readIOUring() throws IllegalAccessException {
        // For more information about io_uring check https://kernel.dk/io_uring.pdf

        // Read the README.md file from this project
        var file1 = workDir + "/README.md";
        int qd = 2; // amount of submission_queue/communication_queue entries to use (the amount of parallel requests that can be send at once) we only need 2 for this example
        var unsafe = MemoryHelper.getUnsafe();

        // Open file to read
        var fd = Syscall.open(file1, FileModes.READ);
        if (fd <= 0) {
            out.println("Unable to open file1 " + SyscallError.valueOf(fd));
            return;
        }

        // Get the file size
        var stat = new CompatStat();
        var fstatRes = Syscall.fstat(fd, stat);
        if (fstatRes < 0) {
            out.printf("Could not fstat file %d\n", fstatRes);
            return;
        }

        // Setup the ring
        var ring = new IoUringParams();
        ring.flags = 0; // We could specify some different flags here
        // Calling setup will give us a file descriptor
        var uringFd = Syscall.io_uring_setup(qd, ring);
        if (uringFd < 0) {
            out.printf("Unable to initialize IO_URING_SETUP: %d\n", uringFd);
            return;
        }
        out.printf("IO_URING fd: %d\n", uringFd);
        out.printf("SQ entries: %d, CQ entries: %d\n", ring.sqEntries, ring.cqEntries);
        // Allocate the shared space, each sq entry pointer is 4 bytes
        var sqSharedPtrSize = ring.sqOff.array + (ring.sqEntries * 4);
        var sqSharedPtr = Syscall.mmap(0, sqSharedPtrSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), uringFd, IoUringOffsets.SQ_RING.getValue());
        if (sqSharedPtr < 0) {
            throw new RuntimeException(String.format("Cannot allocate shared memory: %s\n", SyscallError.valueOf(sqSharedPtr)));
        }
        // Map the shared space
        // Setup the submission queue
        var sqHeadPtr = sqSharedPtr + ring.sqOff.head;
        var sqTailPtr = sqSharedPtr + ring.sqOff.tail;
        var sqMaskPtr = sqSharedPtr + ring.sqOff.ringMask;
        var sqEntriesPtr = sqSharedPtr + ring.sqOff.ringEntries;
        var sqFlagsPtr = sqSharedPtr + ring.sqOff.flags;
        var sqDroppedPtr = sqSharedPtr + ring.sqOff.droppped;
        var sqArrayPtr = sqSharedPtr + ring.sqOff.array;
        // Allocate the SQ entries
        var sqe = new IoUringSqe();
        var sqeSize = sqe.getSize();
        var sqeSpaceSize = ring.sqEntries * sqeSize;
        var sqeSpace = Syscall.mmap(0, sqeSpaceSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), uringFd, IoUringOffsets.SQES.getValue());
        if (sqeSpace < 0) {
            throw new RuntimeException(String.format("Cannot allocate SQ entry memory: %s\n", SyscallError.valueOf(sqeSpace)));
        }
        // Sqoff array ptr
        var array = new long[ring.sqEntries];
        // SQ entries pointer table (IoUringSqe**)
        var sqes = new long[ring.sqEntries];
        for (var i = 0; i < ring.sqEntries; i++) {
            array[i] = sqArrayPtr + (i * 4);
            sqes[i] = sqeSpace + (i * sqeSize);
        }
        // Setup the communication queue
        var cqe = new IoUringCqe();
        var cqeSize = cqe.getSize();
        var cqSharedPtrSize = ring.cqOff.cqes + (ring.cqEntries * cqeSize);
        var cqSharedPtr = Syscall.mmap(0, cqSharedPtrSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), uringFd, IoUringOffsets.CQ_RING.getValue());
        var cqHeadPtr = cqSharedPtr + ring.cqOff.head;
        var cqTailPtr = cqSharedPtr + ring.cqOff.tail;
        var cqMaskPtr = cqSharedPtr + ring.cqOff.ringMask;
        var cqEntriesPtr = cqSharedPtr + ring.cqOff.ringEntries;
        var cqOverflow = cqSharedPtr + ring.cqOff.overflow;
        var cqeSpace = cqSharedPtr + ring.cqOff.cqes;
        var cqes = new long[ring.cqEntries];
        for (var i = 0; i < ring.sqEntries; i++) {
            cqes[i] = cqeSpace + (i * cqeSize);
        }

        // Split into 2 reads
        var size1 = Math.abs(stat.size / 2);
        var size2 = stat.size - size1;
        var addr1 = malloc(size1);
        var addr2 = malloc(size2);
        memset(addr1, size1, 0);
        memset(addr2, size2, 0);

        var tail = unsafe.getInt(sqTailPtr);
        // Check if there is enough queue space for 2 reads
        if (tail + 2 == unsafe.getInt(sqHeadPtr))
            throw new RuntimeException("Queue is full");
        // Read the file in 2 parts so generate 2 buffers
        // uring uses IO_VEC objects which contain the length of the buffer
        var ioVec = new IoVec();
        // Setup first read
        ioVec.base = addr1;
        ioVec.len = size1;
        var ioVecData1 = malloc(ioVec.getSize());
        MemoryHelper.writeToAddress(ioVec, ioVecData1);
        // Setup second read
        ioVec.base = addr2;
        ioVec.len = size2;
        var ioVecData2 = malloc(ioVec.getSize());
        MemoryHelper.writeToAddress(ioVec, ioVecData2);

        // We are going to send 2 reads to the ring buffer so fill 2 SQ elements with offsets and pointers to io_vec
        var index = tail & unsafe.getInt(sqMaskPtr);
        // Create the first submission entry
        MemoryHelper.readAddressAs(sqes[index], sqe, true); // take the sqe at the first index
        sqe.setOpcode(IoUringOpFlags.READV); // we want to read
        sqe.fd = (int) fd; // from the file we opened
        sqe.off = 0; // start from the beginning
        sqe.addr = ioVecData1; // read into the iovec pointer
        sqe.len = (int) size1; // the size of the first part
        sqe.userData = ioVecData1; // this can be anything but lets pass the first buffer so we can read it back from the cqe
        MemoryHelper.writeToAddress(sqe, sqes[index], true); // write the modified sqe back
        unsafe.putInt(array[index], 0); // Update the array
        tail++; // increment the tail

        // Same for the second entry
        MemoryHelper.readAddressAs(sqes[index + 1], sqe, true); // take the sqe at the second index
        sqe.setOpcode(IoUringOpFlags.READV); // we want to read
        sqe.fd = (int) fd; // from the file we opened
        sqe.off = 0; // start from the beginning
        sqe.addr = ioVecData2; // read into the iovec pointer
        sqe.len = (int) size2; // the size of the second part
        sqe.userData = ioVecData2; // this can be anything but lets pass the second buffer so we can read it back from the cqe
        MemoryHelper.writeToAddress(sqe, sqes[index + 1], true); // write the modified sqe back
        unsafe.putInt(array[index + 1], 1); // Update the array
        tail++; // increment the tail

        // Store the new tail
        unsafe.putInt(sqTailPtr, tail);

        // Submit the 2 read requests
        var submitResult = Syscall.io_uring_enter(uringFd, 2, 0, IoUringEnterFlags.ENTER_GETEVENTS, null);
        if (submitResult < 0) {
            out.printf("Error while submitting ring data: %d, %s\n", submitResult, SyscallError.valueOf(submitResult).getMessage());
            return;
        }

        // Block until the reads are complete
        // Reads are complete when the difference between tail and head is the amount of events we send
        while (true) {
            var cqTail = unsafe.getInt(cqTailPtr);
            var cqHead = unsafe.getInt(cqHeadPtr);
            // If we read the amount of calls we inserted we are done
            if(cqTail - cqHead == 2)
                break;
            // This will block until min_complete events have been completed
            submitResult = Syscall.io_uring_enter(uringFd, 0, 1, IoUringEnterFlags.ENTER_GETEVENTS, null);
            if (submitResult < 0) {
                out.printf("Error while submitting ring data: %d, %s\n", submitResult, SyscallError.valueOf(submitResult).getMessage());
                return;
            }
        }

        // Do we have read requests dropped?
        var dropped = unsafe.getInt(sqDroppedPtr);
        if(dropped > 0) {
            out.printf("Requests dropped: %d\n", dropped);
            return;
        }

        // Read the first half from the ring buffer
        // For reading back we use the communication queue
        MemoryHelper.readAddressAs(cqes[index], cqe);
        MemoryHelper.readAddressAs(cqe.userData, ioVec); // we stored the ioVec pointer in the userData object
        var data1 = new byte[(int) ioVec.len];
        for (var i = 0; i < data1.length; i++) {
            data1[i] = unsafe.getByte(ioVec.base + i);
        }
        // Read the second half from the ring buffer
        MemoryHelper.readAddressAs(cqes[index + 1], cqe);
        MemoryHelper.readAddressAs(cqe.userData, ioVec);
        var data2 = new byte[(int) ioVec.len];
        for (var i = 0; i < data2.length; i++) {
            data2[i] = unsafe.getByte(ioVec.base + i);
        }

        // Update the head to let the kernel know we've read the data
        unsafe.putInt(cqHeadPtr, unsafe.getInt(cqHeadPtr) + 2);

        // Merge the arrays
        var data = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data, 0, data1.length);
        System.arraycopy(data2, 0, data, data1.length, data2.length);

        // Print
        out.printf("Result:\n==============================\n%s\n==============================\n", new String(data, StandardCharsets.UTF_8));

        // Cleanup memory
        free(sqSharedPtr, sqSharedPtrSize);
        free(sqeSpace, sqeSpaceSize);
        free(cqSharedPtr, cqSharedPtrSize);
        free(ioVecData2, ioVec.getSize());
        free(ioVecData1, ioVec.getSize());
        free(addr2, size2);
        free(addr1, size1);
        Syscall.close(fd);
    }

    public static void main(String[] args) throws IllegalAccessException {
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
