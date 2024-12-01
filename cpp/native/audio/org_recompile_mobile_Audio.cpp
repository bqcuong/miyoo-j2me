#include <org_recompile_mobile_Audio.h>

#include <SDL2/SDL.h>
#include <SDL2/SDL_mixer.h>
#include <stdio.h>
#include <string>

#define DEFAULT_BGM_FREQ 44100
#define DEFAULT_BGM_FORMAT AUDIO_S16LSB
#define DEFAULT_BGM_CHAN 2
#define DEFAULT_BGM_CHUNK 4096

int initAudio = false;
bool isExit = false;

int volumeLevel = 100;

int getVolumeLevel() {
    return volumeLevel;
}

JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1setVol
(JNIEnv *, jclass, jint vol) {
    if (!initAudio) {
        if (SDL_Init(SDL_INIT_AUDIO) == -1) {
            return;
        }

        if (Mix_OpenAudio(DEFAULT_BGM_FREQ, DEFAULT_BGM_FORMAT, DEFAULT_BGM_CHAN, DEFAULT_BGM_CHUNK) < 0) {
            return;
        }

        initAudio = true;
    }

    volumeLevel = vol;
    Mix_VolumeMusic(volumeLevel);

    //Mix_SetMusicCMD(SDL_getenv("MUSIC_CMD"));
}

JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1destroy
(JNIEnv *, jclass) {
    if (isExit) {
        return;
    }
    isExit = true;

//    if (gBGMMusic) {
//        Mix_FreeMusic(gBGMMusic);
//        gBGMMusic = NULL;
//    }
//
//    if (wave) {
//        Mix_FreeChunk(wave);
//        wave = NULL;
//    }

    Mix_CloseAudio();
    Mix_Quit();
    SDL_Quit();
}
