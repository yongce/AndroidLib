#define LOG_TAG "CommonJni"
#include "CommonJni.h"

/*******************************************************************************
**
** Function:        JNI_OnLoad
**
** Description:     Register all JNI functions with Java Virtual Machine.
**                  jvm: Java Virtual Machine.
**                  reserved: Not used.
**
** Returns:         JNI version.
**
*******************************************************************************/
jint JNI_OnLoad(JavaVM* jvm, void* reserved)
{
    LOGD("JNI_OnLoad...");
    JNIEnv *env = NULL;

    // Check JNI version
    if (jvm->GetEnv ((void **) &env, JNI_VERSION_1_6))
    {
        LOGE("failed to get JVM environment");
        return JNI_ERR;
    }

    if (ycdev_commonjni::register_SysResourceLimitHelper(env) == -1)
    {
        LOGE("failed to register SysResourceLimitHelper");
        return JNI_ERR;
    }

    if (ycdev_commonjni::register_FileStatusHelper(env) == -1)
    {
        LOGE("failed to register FileStatusHelper");
        return JNI_ERR;
    }

    LOGD("JNI_OnLoad done");
    return JNI_VERSION_1_6;
}
