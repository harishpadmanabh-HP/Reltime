//
// Created by Sravan Dath on 17-12-2022.
//

#include <jni.h>
#include <string>


extern "C"
jstring
Java_com_accubits_reltime_utils_AppConfig_developmentBaseUrl(
        JNIEnv* env,
        jclass clazz) {
    std::string baseURL = "https://reltime-api-dev.devtomaster.com/";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_accubits_reltime_utils_AppConfig_preProductionBaseUrl(
        JNIEnv* env,
        jclass clazz) {
    std::string baseURL = "https://reltime-api-preprod.devtomaster.com/";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_accubits_reltime_utils_AppConfig_stagingBaseUrl(
        JNIEnv* env,
        jclass clazz) {
    std::string baseURL = "https://reltime-api-stag.devtomaster.com/";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_accubits_reltime_utils_AppConfig_releaseBaseUrl(
        JNIEnv* env,
        jclass clazz) {
    std::string baseURL = "https://api.reltime.com/";
    return env->NewStringUTF(baseURL.c_str());
}

extern "C"
jstring
Java_com_accubits_reltime_utils_AppConfig_getSiteKey(
        JNIEnv* env,
        jclass clazz) {
    std::string baseURL = "6LdyrWgjAAAAADDV2rNjjaGM-Tz2DAdQFJMfmdh7";
    return env->NewStringUTF(baseURL.c_str());
}

