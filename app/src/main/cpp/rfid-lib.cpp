#include <jni.h>

extern "C" JNIEXPORT jstring
Java_com_usungsoft_usLibrary_utils_RfidUtils_00024Default_getTableConstant(JNIEnv* env, jobject thiz, jint index) {
    switch(index) {
        case 1:
            return env->NewStringUTF("L,Q,H,7,S,M,2,E,V,5,*, ,-");
        case 2:
            return env->NewStringUTF("G,4,N,C,1,R,A,Z,6,K,D,9,Y");
        case 3:
            return env->NewStringUTF("X,T,3,F,B,W,0,U,I,O,8,P,J");
        default:
            return NULL; // or another default value
    }
}