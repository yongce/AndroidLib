LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := ycdev-commonjni

define all-cpp-files-under
$(patsubst ./%,%, \
  $(shell cd $(LOCAL_PATH) ; \
          find $(1) -name "*.cpp" -and -not -name ".*") \
 )
endef

LOCAL_SRC_FILES := $(call all-cpp-files-under, .)

LOCAL_LDLIBS := \
    -llog

include $(BUILD_SHARED_LIBRARY)
