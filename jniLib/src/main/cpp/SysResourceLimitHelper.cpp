#define LOG_TAG "SysResourceLimitHelper"
#include "CommonJni.h"

#include <sys/resource.h>

namespace ycdev_commonjni {

static jclass gLimitInfo_class;
static jmethodID gLimitInfo_constructorMethodId;
static jfieldID gLimitInfo_curLimitFieldId;
static jfieldID gLimitInfo_maxLimitFieldId;

#pragma clang diagnostic push
#pragma ide diagnostic ignored "EmptyDeclOrStmt"
static jobject SysResourceLimitHelper_getOpenFilesLimit(JNIEnv* env, __attribute__((unused)) jobject thiz)
{
    LOGD("to get open files limit");
    struct rlimit limitInfo = {0};
    int result = getrlimit(RLIMIT_NOFILE, &limitInfo);
    if (result != 0)
    {
        LOGW("failed to get open files limit");
        return nullptr;
    }

    int curLimit = (int)limitInfo.rlim_cur;
    int maxLimit = (int)limitInfo.rlim_max;
    LOGD("curLimit: %d, maxLimit: %d", curLimit, maxLimit);

    jobject limitInfoObj = env->NewObject(gLimitInfo_class, gLimitInfo_constructorMethodId);
    env->SetIntField(limitInfoObj, gLimitInfo_curLimitFieldId, curLimit);
    env->SetIntField(limitInfoObj, gLimitInfo_maxLimitFieldId, maxLimit);

    return limitInfoObj;
}
#pragma clang diagnostic pop

#pragma clang diagnostic push
#pragma ide diagnostic ignored "EmptyDeclOrStmt"
static jboolean SysResourceLimitHelper_setOpenFilesLimit(__attribute__((unused)) JNIEnv* env,
                                                         __attribute__((unused)) jobject thiz, jint newLimit)
{
    LOGD("to set open files limit: %d", newLimit);
    struct rlimit limitInfo = {0};
    int result = getrlimit(RLIMIT_NOFILE, &limitInfo);
    if (result != 0)
    {
        LOGW("failed to get open files limit");
        return JNI_FALSE;
    }

    limitInfo.rlim_cur = newLimit;

    result = setrlimit(RLIMIT_NOFILE, &limitInfo);
    if (result != 0)
    {
        LOGW("failed to invoke setrlimit: %d", newLimit);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}
#pragma clang diagnostic pop

///////////////////////////////////////////////////////////////////////////////

static const char* gSysResourceLimitHelper_className =
        "me/ycdev/android/lib/commonjni/SysResourceLimitHelper";
static JNINativeMethod gSysResourceLimitHelper_methods[] = {
    /* name, signature, funcPtr */
    { "getOpenFilesLimit", "()Lme/ycdev/android/lib/commonjni/SysResourceLimitHelper$LimitInfo;",
            (void*) SysResourceLimitHelper_getOpenFilesLimit },
    { "setOpenFilesLimit", "(I)Z",
            (void*) SysResourceLimitHelper_setOpenFilesLimit },
};

static const char* gLimitInfo_className =
        "me/ycdev/android/lib/commonjni/SysResourceLimitHelper$LimitInfo";

static int setupLimitInfoJNI(JNIEnv* env)
{
    jclass limitInfo_class = env->FindClass(gLimitInfo_className);
    if (limitInfo_class == nullptr)
    {
        LOGE("can't find the LimitInfo class");
        return -1;
    }
    gLimitInfo_class = (jclass) env->NewGlobalRef(limitInfo_class);

    gLimitInfo_constructorMethodId = env->GetMethodID(gLimitInfo_class,
            "<init>", "()V");
    if (gLimitInfo_constructorMethodId == nullptr)
    {
        LOGE("can't get constructor of LimitInfo");
        return -1;
    }

    gLimitInfo_curLimitFieldId = env->GetFieldID(gLimitInfo_class, "curLimit", "I");
    if (gLimitInfo_curLimitFieldId == nullptr)
    {
        LOGE("can't get field curLimit of LimitInfo");
        return -1;
    }

    gLimitInfo_maxLimitFieldId = env->GetFieldID(gLimitInfo_class, "maxLimit", "I");
    if (gLimitInfo_maxLimitFieldId == nullptr)
    {
        LOGE("can't get field maxLimit of LimitInfo");
        return -1;
    }

    return 0;
}

/*
 * JNI registration.
 */
int register_SysResourceLimitHelper(JNIEnv* env)
{
    jclass sysResourceLimitHelper = env->FindClass(gSysResourceLimitHelper_className);
    if (sysResourceLimitHelper == nullptr) {
        LOGE("Can't find the SysResourceLimitHelper class");
        return -1;
    }

    if (setupLimitInfoJNI(env) == -1) {
        return -1;
    }

    return env->RegisterNatives(sysResourceLimitHelper, gSysResourceLimitHelper_methods,
            NELEM(gSysResourceLimitHelper_methods));
}

} // namespace ycdev_commonjni
