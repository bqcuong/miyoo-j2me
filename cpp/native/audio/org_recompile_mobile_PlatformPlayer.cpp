#include <org_recompile_mobile_Audio.h>
#include <org_recompile_mobile_PlatformPlayer.h>

#include <SDL2/SDL.h>
#include <SDL2/SDL_mixer.h>
#include <stdio.h>
#include <string>

Mix_Music * gBGMMusic = NULL;
Mix_Chunk * wave = NULL;

bool endsWith(const char * src, const char * str) {
    if (strlen(src) < strlen(str)) {
        return false;
    }
    const char * ptr = src + (strlen(src) - strlen(str));
    for (int i = 0; i < strlen(str); i++) {
        if (ptr[i] != str[i]) {
            return false;
        }
    }
    return true;
}

//JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM * vm, void * reserved) {
//    JNIEnv * env = NULL;
//    if (vm->GetEnv((void ** ) & env, JNI_VERSION_10) != JNI_OK) {
//        return -1;
//    }
//
//    if (env == NULL) {
//        return -1;
//    }
//
//    return JNI_VERSION_10;
//}

void musicFinished() {
    printf("musicFinished\n");
    Mix_HookMusicFinished(NULL);
}

void changeBGM(const char * fname, int loop) {
    if (endsWith(fname, ".mid")) {
        if (Mix_PlayingMusic()) {
            Mix_HaltMusic();
        }

        if (gBGMMusic != NULL) {
            Mix_FreeMusic(gBGMMusic);
            gBGMMusic = NULL;
        }

        gBGMMusic = Mix_LoadMUS(fname);
        if (gBGMMusic != NULL) {
            if (loop != -1) {
                Mix_HookMusicFinished(musicFinished);
            }
            else {
                Mix_HookMusicFinished(NULL);
            }

            if (Mix_PlayMusic(gBGMMusic, loop) < 0) {
                Mix_HookMusicFinished(NULL);
                return;
            }
        }
    }
    else if (endsWith(fname, ".wav")) {
        if (Mix_Playing(0)) {
            Mix_HaltChannel(0);
        }

        if (wave != NULL) {
            Mix_FreeChunk(wave);
            wave = NULL;
        }

        wave = Mix_LoadWAV(fname);
        if (wave != NULL) {
            Mix_VolumeChunk(wave, getVolumeLevel());
            loop = loop > 0 ? loop - 1 : loop; // 0: no loop, -1: infinite loop, n: n times of loop
            Mix_PlayChannel(0, wave, loop);
        }
    }
}

/*
 * Class:     org_recompile_mobile_PlatformPlayer
 * Method:    _start
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_recompile_mobile_PlatformPlayer__1start
(JNIEnv *env, jobject jObj, jstring aBgm, jint loop) {
    const char * str = env->GetStringUTFChars(aBgm, 0);
    changeBGM(str, loop);
    env->ReleaseStringUTFChars(aBgm, str);

//     test callback
    jclass javaClass = env->GetObjectClass(jObj);
    jmethodID callbackMethod = env->GetMethodID(javaClass, "onCallback", "(Ljava/lang/String;)V");
    if (callbackMethod == NULL) {
        printf("Could not find the callback method!\n");
        return; // Handle the error (method not found)
    }
    jstring message = env->NewStringUTF("Hello from C++!");
    env->CallVoidMethod(jObj, callbackMethod, message);
    env->DeleteLocalRef(message);
    env->DeleteLocalRef(javaClass);
}

/*
 * Class:     org_recompile_mobile_PlatformPlayer
 * Method:    _stop
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_recompile_mobile_PlatformPlayer__1stop
(JNIEnv *env, jobject jObj, jint type) {
    if (type == 1) // midi
    {
        if (Mix_PlayingMusic()) {
            Mix_HaltMusic();
        }
    }
    else if (type == 2) // wave
    {
        if (Mix_Playing(0)) {
            Mix_HaltChannel(0);
        }
    }
}
