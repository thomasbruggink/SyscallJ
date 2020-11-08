/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_syscallj_Bridge */

#ifndef _Included_com_syscallj_Bridge
#define _Included_com_syscallj_Bridge
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_syscallj_Bridge
 * Method:    read
 * Signature: (J[BI)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_read
  (JNIEnv *, jclass, jlong, jbyteArray, jint);

/*
 * Class:     com_syscallj_Bridge
 * Method:    write
 * Signature: (J[BI)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_write
  (JNIEnv *, jclass, jlong, jbyteArray, jint);

/*
 * Class:     com_syscallj_Bridge
 * Method:    open
 * Signature: (Ljava/lang/String;IS)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_open
  (JNIEnv *, jclass, jstring, jint, jshort);

/*
 * Class:     com_syscallj_Bridge
 * Method:    close
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_close
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_syscallj_Bridge
 * Method:    fstat
 * Signature: (JLjava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_fstat
  (JNIEnv *, jclass, jlong, jobject);

/*
 * Class:     com_syscallj_Bridge
 * Method:    mmap
 * Signature: (JJJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_mmap
  (JNIEnv *, jclass, jlong, jlong, jlong, jlong, jlong, jlong);

/*
 * Class:     com_syscallj_Bridge
 * Method:    mprotect
 * Signature: (JJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_mprotect
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_syscallj_Bridge
 * Method:    munmap
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_munmap
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_syscallj_Bridge
 * Method:    ioctl
 * Signature: (JJLjava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_ioctl
  (JNIEnv *, jclass, jlong, jlong, jobject);

/*
 * Class:     com_syscallj_Bridge
 * Method:    io_uring_setup
 * Signature: (ILjava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_io_1uring_1setup
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     com_syscallj_Bridge
 * Method:    io_uring_enter
 * Signature: (JIIILjava/lang/Object;)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_io_1uring_1enter
  (JNIEnv *, jclass, jlong, jint, jint, jint, jobject);

/*
 * Class:     com_syscallj_Bridge
 * Method:    io_uring_register
 * Signature: (JI[BI)J
 */
JNIEXPORT jlong JNICALL Java_com_syscallj_Bridge_io_1uring_1register
  (JNIEnv *, jclass, jlong, jint, jbyteArray, jint);

/*
 * Class:     com_syscallj_Bridge
 * Method:    read_address_as
 * Signature: (JLjava/lang/Object;Z)V
 */
JNIEXPORT void JNICALL Java_com_syscallj_Bridge_read_1address_1as
  (JNIEnv *, jclass, jlong, jobject, jboolean);

/*
 * Class:     com_syscallj_Bridge
 * Method:    write_to_address
 * Signature: (Ljava/lang/Object;JZ)V
 */
JNIEXPORT void JNICALL Java_com_syscallj_Bridge_write_1to_1address
  (JNIEnv *, jclass, jobject, jlong, jboolean);

#ifdef __cplusplus
}
#endif
#endif
