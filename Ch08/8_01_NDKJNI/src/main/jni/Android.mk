LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := features
#To build the alternate native lib, change the source file to NativeLibAlternate.c
LOCAL_SRC_FILES := NativeLib.c
LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := cpufeatures

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/cpufeatures)