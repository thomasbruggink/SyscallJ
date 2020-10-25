#include "com_syscallj_Bridge.h"
#include "Syscall.h"
#include <string>
#include <cstring>

using namespace std;
using namespace syscallj;

jbyteArray toJByteArray(JNIEnv *env, char *buffer, int size)
{
    auto jBuffer = env->NewByteArray(size);
    auto copyBuffer = env->GetPrimitiveArrayCritical(jBuffer, 0);
    memcpy(copyBuffer, buffer, size);
    env->ReleasePrimitiveArrayCritical(jBuffer, copyBuffer, 0);
    return jBuffer;
}

void toCharArray(JNIEnv *env, jbyteArray from, char *to)
{
    auto size = env->GetArrayLength(from);
    auto copyBuffer = env->GetPrimitiveArrayCritical(from, 0);
    memcpy(to, copyBuffer, size);
    env->ReleasePrimitiveArrayCritical(from, copyBuffer, 0);
}

jstring Java_com_syscallj_Bridge_read(JNIEnv *env, jclass c, jlong jFd, jint jSize)
{
    auto buffer = new char[jSize];
    auto result = Syscall::read(jFd, buffer, jSize);
    if (result < 0)
    {
        return nullptr;
    }
    return env->NewStringUTF(buffer);
}

jlong Java_com_syscallj_Bridge_write(JNIEnv *env, jclass c, jlong jFd, jstring jBuffer, jint size)
{
    const char *input_ptr = env->GetStringUTFChars(jBuffer, nullptr);
    auto content = string(input_ptr);
    env->ReleaseStringUTFChars(jBuffer, input_ptr);
    return Syscall::write(jFd, content.c_str(), size);
}

jlong Java_com_syscallj_Bridge_open(JNIEnv *env, jclass c, jstring jFileName, jint flags, jshort jMode)
{
    const char *input_ptr = env->GetStringUTFChars(jFileName, nullptr);
    auto content = string(input_ptr);
    env->ReleaseStringUTFChars(jFileName, input_ptr);
    return Syscall::open(content.c_str(), flags, jMode);
}

jlong Java_com_syscallj_Bridge_mmap(JNIEnv * env, jclass c, jlong addr, jlong len, jlong prot, jlong flags, jlong fd, jlong off)
{
    return Syscall::mmap(addr, len, prot, flags, fd, off);
}

jlong Java_com_syscallj_Bridge_mprotect(JNIEnv * env, jclass c, jlong addr, jlong len, jlong prot)
{
    return Syscall::mprotect(addr, len, prot);
}

jlong Java_com_syscallj_Bridge_munmap(JNIEnv * env, jclass c, jlong addr, jlong len)
{
    return Syscall::munmap(addr, len);
}

jlong Java_com_syscallj_Bridge_ioctl(JNIEnv *env, jclass c, jlong fd, jlong cmd, jobject args)
{
    int result;
    if(args == nullptr) {
        result = Syscall::ioctl(fd, cmd, 0);
    }
    else {
        auto writeObject = env->GetMethodID(env->GetObjectClass(args), "writeObject", "([B)V");
        auto readObject = env->GetMethodID(env->GetObjectClass(args), "readObject", "()[B");
        auto argData = (jbyteArray)env->CallObjectMethod(args, readObject);
        auto size = env->GetArrayLength(argData);
        char buffer[size];
        toCharArray(env, argData, buffer);
        result = Syscall::ioctl(fd, cmd, (long int)buffer);
        env->CallVoidMethod(args, writeObject, toJByteArray(env, buffer, size));
    }
    return result;
}

jlong Java_com_syscallj_Bridge_io_1uring_1setup(JNIEnv * env, jclass c, jint entries, jobject params)
{
    return 0;
}

jlong Java_com_syscallj_Bridge_io_1uring_1enter(JNIEnv * env, jclass c, jlong fd, jint to_submit, jint min_complete, jint flags, jint sig)
{
    return 0;
}

jlong Java_com_syscallj_Bridge_io_1uring_1register(JNIEnv * env, jclass c, jlong fd, jint opcode, jlong arg, jint nr_args)
{
    return 0;
}

jlong Java_com_syscallj_Bridge_close(JNIEnv *, jclass c, jlong jFd)
{
    return Syscall::close(jFd);
}
