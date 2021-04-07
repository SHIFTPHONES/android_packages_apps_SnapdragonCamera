/*
Copyright (c) 2016, The Linux Foundation. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of The Linux Foundation nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#include <jni.h>
#include <assert.h>
#include <stdlib.h>

#ifdef __ANDROID__
#include "android/log.h"
#define printf(...) __android_log_print( ANDROID_LOG_ERROR, "ImageUtil", __VA_ARGS__ )
#endif

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL Java_com_android_camera_imageprocessor_PostProcessor_nativeFlipNV21(
        JNIEnv* env, jobject thiz, jbyteArray yvuBytes, jint stride, jint height, jint gap, jboolean isVertical);
#ifdef __cplusplus
}
#endif

typedef unsigned char uint8_t;

jint JNICALL Java_com_android_camera_imageprocessor_PostProcessor_nativeFlipNV21(
        JNIEnv* env, jobject thiz, jbyteArray yvuBytes, jint stride, jint height, jint gap, jboolean isVertical)
{
    (void)thiz;
    jbyte* imageDataNV21Array = env->GetByteArrayElements(yvuBytes, NULL);
    uint8_t *buf = (uint8_t *)imageDataNV21Array;
    int ysize = stride * height;
    uint8_t temp1, temp2;

    if(isVertical) {
        for (int x = 0; x < stride; x++) {
            for (int y = 0; y < height / 2; y++) {
                temp1 = buf[y * stride + x];
                buf[y * stride + x] = buf[(height - 1 - y) * stride + x];
                buf[(height - 1 - y) * stride + x] = temp1;
            }
        }
        for (int x = 0; x < stride; x += 2) {
            for (int y = 0; y < height / 4; y++) {
                temp1 = buf[ysize + y * stride + x];
                temp2 = buf[ysize + y * stride + x + 1];
                buf[ysize + y * stride + x] = buf[ysize + (height / 2 - 1 - y) * stride + x];
                buf[ysize + y * stride + x + 1] = buf[ysize + (height / 2 - 1 - y) * stride + x + 1];
                buf[ysize + (height / 2 - 1 - y) * stride + x] = temp1;
                buf[ysize + (height / 2 - 1 - y) * stride + x + 1] = temp2;
            }
        }
    } else {
        int width = stride - gap;
        for (int x = 0; x < width/2; x++) {
            for (int y = 0; y < height; y++) {
                temp1 = buf[y * stride + x];
                buf[y * stride + x] = buf[y * stride + (width - 1 - x)];
                buf[y * stride + (width - 1 - x)] = temp1;
            }
        }
        for (int x = 0; x < width/2; x += 2) {
            for (int y = 0; y < height / 2; y++) {
                temp1 = buf[ysize + y * stride + x];
                temp2 = buf[ysize + y * stride + x + 1];
                buf[ysize + y * stride + x] = buf[ysize + y * stride + (width - 1 - x - 1)];
                buf[ysize + y * stride + x + 1] = buf[ysize + y * stride + (width - 1 - x)];
                buf[ysize + y * stride + (width - 1 - x - 1)] = temp1;
                buf[ysize + y * stride + (width - 1 - x)] = temp2;
            }
        }
    }

    env->ReleaseByteArrayElements(yvuBytes, imageDataNV21Array, JNI_ABORT);
    return 0;
}
