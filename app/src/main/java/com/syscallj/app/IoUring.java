package com.syscallj.app;

import com.syscallj.MemoryHelper;
import com.syscallj.Syscall;
import com.syscallj.SyscallError;
import com.syscallj.enums.*;
import com.syscallj.models.IoUringCqe;
import com.syscallj.models.IoUringParams;
import com.syscallj.models.IoUringSqe;
import com.syscallj.models.IoVec;
import sun.misc.Unsafe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IoUring implements AutoCloseable {
    private final int ringFd;
    private final IoUringParams ring;
    private final IoUringSq sq;
    private final IoUringCq cq;

    private final Map<Long, CompletableFuture<byte[]>> readCallbacks;
    private final Map<Long, CompletableFuture<Integer>> writeCallbacks;
    private final long[] ioVecDataPtr;
    private final int ioVecDataPtrSize;

    private final Unsafe unsafe;

    private boolean blockLoop;
    private boolean loop = false;
    private CompletableFuture<Void> loopStopped;
    private Thread loopThread;

    public IoUring(int queueSize) {
        unsafe = MemoryHelper.getUnsafe();
        readCallbacks = new HashMap<>();
        writeCallbacks = new HashMap<>();
        ring = new IoUringParams();
        // Calling setup will give us a file descriptor, returns long but IOUring works with integers only.
        ringFd = (int) Syscall.io_uring_setup(queueSize, ring);
        if (ringFd < 0) {
            throw new RuntimeException(String.format("Unable to initialize IO_URING_SETUP: %d", ringFd));
        }
        try {
            sq = new IoUringSq();
            cq = new IoUringCq();
            ioVecDataPtrSize = (new IoVec()).getSize();
            ioVecDataPtr = new long[ring.sqEntries];
            for(var i = 0; i < ring.sqEntries; i++) {
                ioVecDataPtr[i] = MemoryHelper.malloc(ioVecDataPtrSize);
                if(ioVecDataPtr[i] < 0) {
                    throw new RuntimeException(String.format("Unable to allocate io vec memory: %s", SyscallError.valueOf(ioVecDataPtr[i])));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int poll() {
        var processed = 0;
        var head = cq.getHead();
        while (head < cq.getTail()) {
            var index = head & cq.getMask();
            var cqe = cq.getCqeAt(index);
            var ioVec = new IoVec();
            MemoryHelper.readAddressAs(cqe.userData, ioVec);
            if(readCallbacks.containsKey(ioVec.base)) {
                var data = new byte[(int) cqe.res];
                for (var i = 0; i < data.length; i++) {
                    data[i] = unsafe.getByte(ioVec.base + i);
                }
                var callback = readCallbacks.get(ioVec.base);
                if(cqe.res >= 0)
                    callback.complete(data);
                else
                    callback.completeExceptionally(new RuntimeException(String.format("Read failed: %s", SyscallError.valueOf(cqe.res))));
                readCallbacks.remove(ioVec.base);
            }
            else {
                var callback = writeCallbacks.get(ioVec.base);
                if(cqe.res >= 0)
                    callback.complete(cqe.res);
                else
                    callback.completeExceptionally(new RuntimeException(String.format("Write failed: %s", SyscallError.valueOf(cqe.res))));
                writeCallbacks.remove(ioVec.base);
            }
            MemoryHelper.free(ioVec.base, ioVec.len);
            head++;
            processed++;
        }
        cq.setHead(head);
        return processed;
    }

    public void startPolling(boolean block) {
        blockLoop = block;
        loop = true;
        loopThread = new Thread(this::internalLoop);
        loopThread.start();
    }

    public CompletableFuture<Void> stopPolling() {
        loop = false;
        return loopStopped;
    }

    private void internalLoop() {
        loopStopped = new CompletableFuture<>();
        try {
            while (loop) {
                poll();
                if(blockLoop) {
                    // This will block until min_complete events have been completed
                    var submitResult = Syscall.io_uring_enter(ringFd, 0, 1, IoUringEnterFlags.ENTER_GETEVENTS, null);
                    if (submitResult < 0) {
                        throw new RuntimeException(String.format("Error while submitting ring data: %s", SyscallError.valueOf(submitResult)));
                    }
                }
                else {
                    Thread.sleep(1);
                }
            }
            loopStopped.complete(null);
        }
        catch (Exception ex) {
            loopStopped.completeExceptionally(ex);
        }
    }

    /**
     * Schedules a read from a file.
     * @param fd file descriptor to read from
     * @param size amount of bytes to read
     * @return future that will be completed when the read has completed
     */
    public CompletableFuture<byte[]> queueRead(int fd, int size, int offset) {
        var addr = MemoryHelper.malloc(size);
        if(addr < 0) {
            throw new RuntimeException(String.format("Cannot allocate io vec read memory: %s", SyscallError.valueOf(addr)));
        }

        queue(IoUringOpFlags.READV, fd, offset, addr, size);

        var callback = new CompletableFuture<byte[]>();
        readCallbacks.put(addr, callback);
        return callback;
    }

    /**
     * Schedules a write to a file.
     * @param fd file descriptor to write to
     * @param data amount of bytes to read
     * @param offset offset to write from
     * @return future that will be completed when the write has completed
     */
    public CompletableFuture<Integer> queueWrite(int fd, byte[] data, int offset) {
        var addr = MemoryHelper.malloc(data.length);
        if(addr < 0) {
            throw new RuntimeException(String.format("Cannot allocate io vec write memory: %s", SyscallError.valueOf(addr)));
        }
        for(var i = 0; i < data.length; i++) {
            unsafe.putByte(addr + i, data[i]);
        }

        queue(IoUringOpFlags.WRITEV, fd, offset, addr, data.length);

        var callback = new CompletableFuture<Integer>();
        writeCallbacks.put(addr, callback);
        return callback;
    }

    public void queue(IoUringOpFlags op, int fd, int offset, long addr, long size) {
        var tail = sq.getTail();
        if (tail + 1 == sq.getHead())
            throw new RuntimeException("Queue is full");

        var ioVec = new IoVec();
        ioVec.base = addr;
        ioVec.len = size;
        MemoryHelper.writeToAddress(ioVec, ioVecDataPtr[tail]);

        var index = tail & sq.getMask();
        var sqe = sq.getSqeAt(index);
        sqe.setOpcode(op);
        sqe.fd = fd;
        sqe.off = offset;
        sqe.addr = ioVecDataPtr[tail];
        sqe.len = ioVecDataPtrSize;
        sqe.userData = ioVecDataPtr[tail];
        sq.setSqeAt(index, sqe);
        sq.setTail(tail + 1);
    }

    public void submitQueue() {
        submitQueue(0);
    }

    public void submitQueue(int waitFor) {
        var submitResult = Syscall.io_uring_enter(ringFd, sq.getTail() - sq.getHead(), waitFor, IoUringEnterFlags.ENTER_GETEVENTS, null);
        if (submitResult < 0) {
            throw new RuntimeException(String.format("Error while submitting ring data: %s", SyscallError.valueOf(submitResult)));
        }
    }

    @Override
    public void close() {
        if(loopThread != null) {
            try {
                stopPolling().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sq.close();
        cq.close();
        for(var ptr : ioVecDataPtr) {
            MemoryHelper.free(ptr, ioVecDataPtrSize);
        }
    }

    private class IoUringSq implements AutoCloseable {
        final long head;
        final long tail;
        final long mask;
        final long entries;
        final long flags;
        final long dropped;
        final long arrayPtr;
        final long[] array;

        final long sharedPtr;
        final int sharedPtrSize;

        final long entryPtr;
        final int entryPtrSize;
        final long[] sqeEntries;

        public IoUringSq() throws IllegalAccessException {
            sharedPtrSize = ring.sqOff.array + (ring.sqEntries * 4);
            sharedPtr = Syscall.mmap(0, sharedPtrSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), ringFd, IoUringOffsets.SQ_RING.getValue());
            if (sharedPtr < 0) {
                throw new RuntimeException(String.format("Cannot allocate uring submission queue memory: %s\n", SyscallError.valueOf(sharedPtr)));
            }
            head = sharedPtr + ring.sqOff.head;
            tail = sharedPtr + ring.sqOff.tail;
            mask = sharedPtr + ring.sqOff.ringMask;
            entries = sharedPtr + ring.sqOff.ringEntries;
            flags = sharedPtr + ring.sqOff.flags;
            dropped = sharedPtr + ring.sqOff.droppped;
            arrayPtr = sharedPtr + ring.sqOff.array;

            var sqe = new IoUringSqe();
            var sqeSize = sqe.getSize();
            entryPtrSize = ring.sqEntries * sqeSize;
            entryPtr = Syscall.mmap(0, entryPtrSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), ringFd, IoUringOffsets.SQES.getValue());
            if (entryPtr < 0) {
                throw new RuntimeException(String.format("Cannot allocate uring submission queue entry memory: %s\n", SyscallError.valueOf(entryPtr)));
            }

            // SQ entries pointer table (IoUringSqe**)
            array = new long[ring.sqEntries];
            sqeEntries = new long[ring.sqEntries];
            for (var i = 0; i < ring.sqEntries; i++) {
                array[i] = arrayPtr + (i * 4);
                sqeEntries[i] = entryPtr + (i * sqeSize);
            }
        }

        public int getHead() {
            return unsafe.getInt(head);
        }

        public int getTail() {
            return unsafe.getInt(tail);
        }

        public void setTail(int val) {
            unsafe.putInt(tail, val);
        }

        public int getMask() {
            return unsafe.getInt(mask);
        }

        public IoUringSqe getSqeAt(int index) {
            var sqe = new IoUringSqe();
            MemoryHelper.readAddressAs(sqeEntries[index], sqe);
            return sqe;
        }

        public void setSqeAt(int index, IoUringSqe sqe) {
            unsafe.putInt(array[index], index);
            MemoryHelper.writeToAddress(sqe, sqeEntries[index]);
        }

        @Override
        public void close() {
            MemoryHelper.free(sharedPtr, sharedPtrSize);
            MemoryHelper.free(entryPtr, entryPtrSize);
        }
    }

    private class IoUringCq implements AutoCloseable {
        final long head;
        final long tail;
        final long mask;
        final long entries;
        final long overflow;

        final long sharedPtr;
        final int sharedPtrSize;

        final long entryPtr;
        final long[] cqeEntries;

        public IoUringCq() throws IllegalAccessException {
            var cqe = new IoUringCqe();
            var cqeSize = cqe.getSize();
            sharedPtrSize = ring.cqOff.cqes + (ring.cqEntries * cqeSize);
            sharedPtr = Syscall.mmap(0, sharedPtrSize, MemoryProtection.READ.getValue() | MemoryProtection.WRITE.getValue(), MemoryFlags.SHARED.getValue() | MemoryFlags.POPULATE.getValue(), ringFd, IoUringOffsets.CQ_RING.getValue());
            if (sharedPtr < 0) {
                throw new RuntimeException(String.format("Cannot allocate uring communication queue memory: %s\n", SyscallError.valueOf(sharedPtr)));
            }

            head = sharedPtr + ring.cqOff.head;
            tail = sharedPtr + ring.cqOff.tail;
            mask = sharedPtr + ring.cqOff.ringMask;
            entries = sharedPtr + ring.cqOff.ringEntries;
            overflow = sharedPtr + ring.cqOff.overflow;

            entryPtr = sharedPtr + ring.cqOff.cqes;
            cqeEntries = new long[ring.cqEntries];
            for (var i = 0; i < ring.sqEntries; i++) {
                cqeEntries[i] = entryPtr + (i * cqeSize);
            }
        }

        @Override
        public void close() {
            MemoryHelper.free(sharedPtr, sharedPtrSize);
        }

        public int getHead() {
            return unsafe.getInt(head);
        }

        public void setHead(int val) {
            unsafe.putInt(head, val);
        }

        public int getTail() {
            return unsafe.getInt(tail);
        }

        public int getMask() {
            return unsafe.getInt(mask);
        }

        public IoUringCqe getCqeAt(int index) {
            var cqe = new IoUringCqe();
            MemoryHelper.readAddressAs(cqeEntries[index], cqe);
            return cqe;
        }
    }
}
