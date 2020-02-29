#ifndef _YCDEV_COMMON_JNI_H_
#define _YCDEV_COMMON_JNI_H_

#include <jni.h>
#include <cstddef>
#include <cerrno>
#include <cstring>
#include <android/log.h>

// log helpers
#ifndef COMMON_JNI_DEBUG
    #define LOGV(...)
    #define LOGD(...)
#else
    #define LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
    #define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#endif
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

// array size
#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

extern "C"
{
    jint JNI_OnLoad(JavaVM* jvm, void* reserved);
}

namespace ycdev_commonjni {
    // register functions
    int register_SysResourceLimitHelper(JNIEnv* env);
    int register_FileStatusHelper (JNIEnv* env);

} // namespace ycdev_commonjni

#endif // _YCDEV_COMMON_JNI_H_
