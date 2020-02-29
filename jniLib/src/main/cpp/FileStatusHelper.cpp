#define LOG_TAG "FileStatusHelper"
#include "CommonJni.h"

#include <sys/stat.h>

namespace ycdev_commonjni {

static jclass gFileStatus_class;
static jmethodID gFileStatus_constructorMethodId;
static jfieldID gFileStatus_uidFieldId;
static jfieldID gFileStatus_gidFieldId;
static jfieldID gFileStatus_modeFieldId;

static jobject FileStatusHelper_getFileStatus(JNIEnv* env, jobject thiz, jstring filePath)
{
    const char* nativeFilePath = env->GetStringUTFChars(filePath, JNI_FALSE);
    LOGD("to get file stat [%s]", nativeFilePath);
    struct stat statInfo;
    int result = stat(nativeFilePath, &statInfo);
    int statErrno = errno;
    env->ReleaseStringUTFChars(filePath, nativeFilePath);

    if (result != 0)
    {
        LOGW("failed to get file stat [%s]", strerror(statErrno));
        return NULL;
    }

    LOGD("uid [%d], gid[%d], mode [%o]", statInfo.st_uid, statInfo.st_gid, statInfo.st_mode);
    jobject fileStatusObj = env->NewObject(gFileStatus_class, gFileStatus_constructorMethodId);
    env->SetIntField(fileStatusObj, gFileStatus_uidFieldId, statInfo.st_uid);
    env->SetIntField(fileStatusObj, gFileStatus_gidFieldId, statInfo.st_gid);
    env->SetIntField(fileStatusObj, gFileStatus_modeFieldId, statInfo.st_mode);

    return fileStatusObj;
}

///////////////////////////////////////////////////////////////////////////////

static const char* gFileStatusHelper_className =
        "me/ycdev/android/lib/commonjni/FileStatusHelper";
static JNINativeMethod gFileStatusHelper_methods[] = {
        /* name, signature, funcPtr */
        { "getFileStatus", "(Ljava/lang/String;)Lme/ycdev/android/lib/commonjni/FileStatusHelper$FileStatus;",
                (void*) FileStatusHelper_getFileStatus },
};

static const char* gFileStatus_className =
        "me/ycdev/android/lib/commonjni/FileStatusHelper$FileStatus";

static int setupFileStatusJNI(JNIEnv* env)
{
    jclass fileStatus_class = env->FindClass(gFileStatus_className);
    if (fileStatus_class == NULL)
    {
        LOGE("can't find the FileStatus class");
        return -1;
    }
    gFileStatus_class = (jclass) env->NewGlobalRef(fileStatus_class);

    gFileStatus_constructorMethodId = env->GetMethodID(gFileStatus_class,
                                                       "<init>", "()V");
    if (gFileStatus_constructorMethodId == NULL)
    {
        LOGE("can't get constructor of FileStatus");
        return -1;
    }

    gFileStatus_uidFieldId = env->GetFieldID(gFileStatus_class, "uid", "I");
    if (gFileStatus_uidFieldId == NULL)
    {
        LOGE("can't get field uid of FileStatus");
        return -1;
    }

    gFileStatus_gidFieldId = env->GetFieldID(gFileStatus_class, "gid", "I");
    if (gFileStatus_gidFieldId == NULL)
    {
        LOGE("can't get field gid of FileStatus");
        return -1;
    }

    gFileStatus_modeFieldId = env->GetFieldID(gFileStatus_class, "mode", "I");
    if (gFileStatus_modeFieldId == NULL)
    {
        LOGE("can't get field mode of FileStatus");
        return -1;
    }

    return 0;
}

/*
 * JNI registration.
 */
int register_FileStatusHelper (JNIEnv* env)
{
    jclass fileStatusHelper = env->FindClass(gFileStatusHelper_className);
    if (fileStatusHelper == NULL) {
        LOGE("Can't find the FileStatusHelper class");
        return -1;
    }

    if (setupFileStatusJNI(env) == -1) {
        return -1;
    }

    return env->RegisterNatives(fileStatusHelper, gFileStatusHelper_methods,
                                NELEM(gFileStatusHelper_methods));
}

} // namespace ycdev_commonjni
