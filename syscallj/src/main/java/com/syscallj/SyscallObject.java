package com.syscallj;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class SyscallObject {
    public int getSize() throws IllegalAccessException {
        var fields = this.getClass().getDeclaredFields();
        var size = 0;
        for (var field : fields) {
            var type = field.getType().getTypeName();
            switch (type) {
                case "long": {
                    size += 8;
                    break;
                }
                case "int": {
                    size += 4;
                    break;
                }
                case "short": {
                    size += 2;
                    break;
                }
                case "long[]": {
                    long[] result = (long[]) field.get(this);
                    size += result.length * 8;
                    break;
                }
                case "int[]": {
                    int[] result = (int[]) field.get(this);
                    size += result.length * 4;
                    break;
                }
                case "byte[]": {
                    byte[] result = (byte[]) field.get(this);
                    size += result.length;
                    break;
                }
                case "char[]": {
                    char[] result = (char[]) field.get(this);
                    size += result.length;
                    break;
                }
                case "byte":
                case "char": {
                    size += 1;
                    break;
                }
                default: {
                    if (field.getType().isArray()) {
                        var arrayData = (Object[]) field.get(this);
                        if (arrayData.getClass().getComponentType().getSuperclass() == SyscallObject.class) {
                            if (arrayData.length == 0) {
                                break;
                            }
                            var syscallArrayData = (SyscallObject[]) arrayData;
                            size += syscallArrayData[0].getSize() * syscallArrayData.length;
                            break;
                        }
                    } else {
                        var obj = (Object) field.get(this);
                        if (obj.getClass().getSuperclass() == SyscallObject.class) {
                            var item = (SyscallObject) obj;
                            size += item.getSize();
                            break;
                        }
                    }
                    System.out.println("Unknown size " + type);
                }
            }
        }
        return size;
    }

    public void writeObject(byte[] data) throws IllegalAccessException {
        var wrapper = ByteBuffer.wrap(data);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        var fields = this.getClass().getDeclaredFields();
        for (var field : fields) {
            var type = field.getType().getTypeName();
            switch (type) {
                case "long": {
                    field.set(this, wrapper.getLong());
                    break;
                }
                case "int": {
                    field.set(this, wrapper.getInt());
                    break;
                }
                case "short": {
                    field.set(this, wrapper.getShort());
                    break;
                }
                case "long[]": {
                    var result = (long[]) field.get(this);
                    for (var i = 0; i < result.length; i++) {
                        result[i] = wrapper.getLong();
                    }
                    break;
                }
                case "int[]": {
                    var result = (int[]) field.get(this);
                    for (var i = 0; i < result.length; i++) {
                        result[i] = wrapper.getInt();
                    }
                    break;
                }
                case "byte[]": {
                    var result = (byte[]) field.get(this);
                    wrapper.get(result, 0, result.length - 1);
                    break;
                }
                case "byte": {
                    field.set(this, (byte) wrapper.getChar());
                    break;
                }
                case "char": {
                    field.set(this, wrapper.getChar());
                    break;
                }
                default: {
                    if (field.getType().isArray()) {
                        var arrayData = (Object[]) field.get(this);
                        if (arrayData.getClass().getComponentType().getSuperclass() == SyscallObject.class) {
                            if (arrayData.length == 0) {
                                break;
                            }
                            var syscallArrayData = (SyscallObject[]) arrayData;
                            var size = syscallArrayData[0].getSize();
                            var buffer = new byte[size];
                            for (var item : syscallArrayData) {
                                wrapper.get(buffer, 0, size);
                                item.writeObject(buffer);
                            }
                            break;
                        }
                    } else {
                        var obj = (Object) field.get(this);
                        if (obj.getClass().getSuperclass() == SyscallObject.class) {
                            var item = (SyscallObject) obj;
                            var size = item.getSize();
                            var buffer = new byte[size];
                            wrapper.get(buffer, 0, size);
                            item.writeObject(buffer);
                            break;
                        }
                    }
                    System.out.println("Unknown type " + type);
                }
            }
        }
    }

    public byte[] readObject() throws IllegalAccessException {
        byte[] data = new byte[getSize()];
        var wrapper = ByteBuffer.wrap(data);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        var fields = this.getClass().getDeclaredFields();
        for (var field : fields) {
            var type = field.getType().getTypeName();
            switch (type) {
                case "long": {
                    wrapper.putLong(field.getLong(this));
                    break;
                }
                case "int": {
                    wrapper.putInt(field.getInt(this));
                    break;
                }
                case "short": {
                    wrapper.putShort(field.getShort(this));
                    break;
                }
                case "long[]": {
                    var result = (long[]) field.get(this);
                    for (var i : result) {
                        wrapper.putLong(i);
                    }
                    break;
                }
                case "int[]": {
                    var result = (int[]) field.get(this);
                    for (var i : result) {
                        wrapper.putInt(i);
                    }
                    break;
                }
                case "byte[]": {
                    wrapper.put((byte[]) field.get(this));
                    break;
                }
                case "byte": {
                    wrapper.put(field.getByte(this));
                    break;
                }
                case "char": {
                    wrapper.putChar(field.getChar(this));
                    break;
                }
                default: {
                    if (field.getType().isArray()) {
                        var arrayData = (Object[]) field.get(this);
                        if (arrayData.getClass().getComponentType().getSuperclass() == SyscallObject.class) {
                            if (arrayData.length == 0) {
                                break;
                            }
                            for (var item : (SyscallObject[]) arrayData) {
                                wrapper.put(item.readObject());
                            }
                            break;
                        }
                    } else {
                        var obj = (Object) field.get(this);
                        if (obj.getClass().getSuperclass() == SyscallObject.class) {
                            wrapper.put(((SyscallObject) obj).readObject());
                            break;
                        }
                    }
                    System.out.println("Unknown type " + type);
                }
            }
        }
        return data;
    }
}
