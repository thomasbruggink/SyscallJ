#include "com_syscallj_Bridge.h"
#include "Syscall.h"
#include <sys/stat.h>
#include <string>
#include <cstring>

using namespace std;
using namespace syscallj;

int readSize(JNIEnv *env, jobject args) {
    auto getSize = env->GetMethodID(env->GetObjectClass(args), "getSize", "()I");
    return env->CallIntMethod(args, getSize);
}

jbyteArray toJByteArray(JNIEnv *env, const char *buffer, jbyteArray jBuffer, int size)
{
    auto copyBuffer = env->GetPrimitiveArrayCritical(jBuffer, 0);
    memcpy(copyBuffer, buffer, size);
    env->ReleasePrimitiveArrayCritical(jBuffer, copyBuffer, 0);
    return jBuffer;
}

jbyteArray toJByteArray(JNIEnv *env, const char *buffer, int size)
{
    auto jBuffer = env->NewByteArray(size);
    return toJByteArray(env, buffer, jBuffer, size);
}

void toCharArray(JNIEnv *env, jbyteArray from, char *to)
{
    auto size = env->GetArrayLength(from);
    auto copyBuffer = env->GetPrimitiveArrayCritical(from, 0);
    memcpy(to, copyBuffer, size);
    env->ReleasePrimitiveArrayCritical(from, copyBuffer, 0);
}

void writeToJava(JNIEnv *env, char *buffer, jobject args, int size)
{
    auto writeObject = env->GetMethodID(env->GetObjectClass(args), "writeObject", "([B)V");
    env->CallVoidMethod(args, writeObject, toJByteArray(env, buffer, size));
}

char *readFromJava(JNIEnv *env, jobject args, int *size)
{
    auto readObject = env->GetMethodID(env->GetObjectClass(args), "readObject", "()[B");
    auto argData = (jbyteArray)env->CallObjectMethod(args, readObject);
    *size = env->GetArrayLength(argData);
    char *buffer = new char[*size];
    toCharArray(env, argData, buffer);
    return buffer;
}

void readFromJava(JNIEnv *env, jobject args, char* ptr, int *size)
{
    auto readObject = env->GetMethodID(env->GetObjectClass(args), "readObject", "()[B");
    auto argData = (jbyteArray)env->CallObjectMethod(args, readObject);
    toCharArray(env, argData, ptr);
}

jlong Java_com_syscallj_Bridge_read(JNIEnv *env, jclass c, jlong jFd, jbyteArray jBuffer, jint jSize)
{
    char buffer[jSize];
    toCharArray(env, jBuffer, buffer);
    auto result = Syscall::read(jFd, buffer, jSize);
    if (result < 0)
    {
        return result;
    }
    toJByteArray(env, buffer, jBuffer, result);
    return result;
}

jlong Java_com_syscallj_Bridge_write(JNIEnv *env, jclass c, jlong jFd, jbyteArray jBuffer, jint size)
{
    char buffer[size];
    toCharArray(env, jBuffer, buffer);
    return Syscall::write(jFd, buffer, size);
}

jlong Java_com_syscallj_Bridge_open(JNIEnv *env, jclass c, jstring jFileName, jint flags, jshort jMode)
{
    const char *input_ptr = env->GetStringUTFChars(jFileName, nullptr);
    auto content = string(input_ptr);
    env->ReleaseStringUTFChars(jFileName, input_ptr);
    return Syscall::open(content.c_str(), flags, jMode);
}

jlong Java_com_syscallj_Bridge_close(JNIEnv *, jclass c, jlong jFd)
{
    return Syscall::close(jFd);
}

jlong Java_com_syscallj_Bridge_fstat(JNIEnv *env, jclass c, jlong jFd, jobject jCompatStat)
{
    int size;
    struct stat *buffer = (struct stat *)readFromJava(env, jCompatStat, &size);
    auto result = Syscall::fstat(jFd, (const char *)buffer);
    writeToJava(env, (char *)buffer, jCompatStat, size);
    delete buffer;
    return result;
}

jlong Java_com_syscallj_Bridge_mmap(JNIEnv *env, jclass c, jlong addr, jlong len, jlong prot, jlong flags, jlong fd, jlong off)
{
    return Syscall::mmap(addr, len, prot, flags, fd, off);
}

jlong Java_com_syscallj_Bridge_mprotect(JNIEnv *env, jclass c, jlong addr, jlong len, jlong prot)
{
    return Syscall::mprotect(addr, len, prot);
}

jlong Java_com_syscallj_Bridge_munmap(JNIEnv *env, jclass c, jlong addr, jlong len)
{
    return Syscall::munmap(addr, len);
}

jlong Java_com_syscallj_Bridge_ioctl(JNIEnv *env, jclass c, jlong fd, jlong cmd, jobject args)
{
    int result;
    if (args == nullptr)
    {
        result = Syscall::ioctl(fd, cmd, 0);
    }
    else
    {
        int size;
        auto buffer = readFromJava(env, args, &size);
        result = Syscall::ioctl(fd, cmd, (long int)buffer);
        writeToJava(env, buffer, args, size);
        delete buffer;
    }
    return result;
}

jlong Java_com_syscallj_Bridge_io_1uring_1setup(JNIEnv *env, jclass c, jint entries, jobject params)
{
    int size;
    auto buffer = readFromJava(env, params, &size);
    auto ioUringParams = (io_uring_params *)buffer;
    auto result = Syscall::io_uring_setup(entries, ioUringParams);
    writeToJava(env, buffer, params, size);
    delete buffer;
    return result;
}

jlong Java_com_syscallj_Bridge_io_1uring_1enter(JNIEnv *env, jclass c, jlong fd, jint to_submit, jint min_complete, jint flags, jobject jSig)
{
    int result;
    if (jSig == nullptr)
    {
        result = Syscall::io_uring_enter(fd, to_submit, min_complete, flags, nullptr);
    }
    else
    {
        int size;
        auto buffer = readFromJava(env, jSig, &size);
        auto sig = (sigset_t *)buffer;
        result = Syscall::io_uring_enter(fd, to_submit, min_complete, flags, sig);
        writeToJava(env, buffer, jSig, size);
        delete buffer;
    }
    return result;
}

jlong Java_com_syscallj_Bridge_io_1uring_1register(JNIEnv *env, jclass c, jlong fd, jint opcode, jbyteArray jbyteArray, jint nr_args)
{
    return 0;
}

void Java_com_syscallj_Bridge_read_1address_1as(JNIEnv * env, jclass c, jlong addr, jobject dest)
{
    auto ptr = (char*)addr;
    auto size = readSize(env, dest);
    writeToJava(env, ptr, dest, size);
}

void Java_com_syscallj_Bridge_write_1to_1address(JNIEnv *env, jclass c, jobject args, jlong addr)
{
    auto ptr = (char*)addr;
    int size;
    readFromJava(env, args, ptr, &size);
}
