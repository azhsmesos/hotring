#include "com_hotring_util_PhysicalAddress.h"
#include <stdio.h>

JNIEXPORT jlong JNICALL Java_com_hotring_util_PhysicalAddress_callAddress
  (JNIEnv *, jclass, jobject obj) {
    return (jlong) obj;
  }