LOCAL_PATH:= $(call my-dir)

# ImageUtilForCamera2 with beautification
include $(CLEAR_VARS)
LOCAL_LDFLAGS   := -llog
LOCAL_SDK_VERSION := 9
LOCAL_MODULE    := libjni_snapimageutil
LOCAL_MODULE_TAGS := optional
LOCAL_PRODUCT_MODULE := true
LOCAL_SRC_FILES := image_util_jni.cpp
LOCAL_CFLAGS    += -ffast-math -O3 -funroll-loops
include $(BUILD_SHARED_LIBRARY)

