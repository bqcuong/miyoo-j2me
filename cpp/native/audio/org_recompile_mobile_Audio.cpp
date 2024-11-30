/* DO NOT EDIT THIS FILE - it is machine generated */
#include <org_recompile_mobile_Audio.h>
#include <org_recompile_mobile_PlatformPlayer.h>
/* Header for class org_recompile_mobile_Audio */

#include <SDL2/SDL.h>
#include <SDL2/SDL_mixer.h>
#include <stdio.h>
#include <string>

#define DEFAULT_BGM_FREQ    44100
#define DEFAULT_BGM_FORMAT  AUDIO_S16LSB
#define DEFAULT_BGM_CHAN    2
#define DEFAULT_BGM_CHUNK   4096


Mix_Music *gBGMMusic = NULL;
Mix_Chunk *wave = NULL;

int initAudio=0;

int volumLevel=100;
char name[100];

bool isExit=false;

JavaVM *m_jvm=NULL;

jobject m_jobj=NULL;

jmethodID m_onCallBack=NULL;
std::string m_onCallBackMethod = "musicFinish";

bool endWith(const char* src, const char* str)
{
	if (strlen(src) < strlen(str)) {
		return false;
	}
	const char* ptr = src+(strlen(src)-strlen(str));
	for (int i = 0; i < strlen(str); i++) {
		if (ptr[i] != str[i]) {
			return false;
		}
	}
	return true;
}

JNIEXPORT jint JNICALL  JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;
    if(vm->GetEnv((void**) &env, JNI_VERSION_10) != JNI_OK) {
       return -1;
    }
 
	if(env == NULL)
	{
		return -1;
	}

    return JNI_VERSION_10;
}

bool isAttachedCurrentThread(JNIEnv** env)
{
	if(m_jvm->GetEnv((void**)env, JNI_VERSION_10) != JNI_OK) {
	    m_jvm->AttachCurrentThread((void**)env, NULL);
	    return true;
	}	
	return false;
}

void musicFinished() {
    //printf("%s music end\n",name);

	Mix_HookMusicFinished(NULL);

	JNIEnv *env = NULL;
	bool isAttached = isAttachedCurrentThread(&env);
	
	env->CallVoidMethod(m_jobj, m_onCallBack);
	
	if (isAttached) {
		m_jvm->DetachCurrentThread();
	}
	
}

void quit()
{
	if(isExit)
	{
		return;
	}
	
	isExit=true;

	if(m_jvm)
	{
		if(m_jobj!=NULL)
		{
			JNIEnv *env = NULL;
			bool isAttached = isAttachedCurrentThread(&env);
			env->DeleteGlobalRef(m_jobj);
			if (isAttached) {
				m_jvm->DetachCurrentThread();
			}
		}
	}
	
	
	if(gBGMMusic) 
	{
		Mix_FreeMusic(gBGMMusic);
		gBGMMusic=NULL;
	}
	
	if (wave) {
        Mix_FreeChunk(wave);
        wave = NULL;
    }
	
	Mix_CloseAudio();
	Mix_Quit();
	SDL_Quit();
	
	
}

void changeBGM(const char* fname,int loop)
{	
	if(endWith(fname,".mid"))
	{
		if(Mix_PlayingMusic()) {
			Mix_HaltMusic();
		}
		
		if(gBGMMusic!=NULL) 
		{
			Mix_FreeMusic(gBGMMusic);
			gBGMMusic=NULL;
		}
		
		gBGMMusic = Mix_LoadMUS(fname);
		if(gBGMMusic!=NULL) {
			if(loop!=-1)
			{
				//strcpy(name,fname);
				Mix_HookMusicFinished(musicFinished);
			}
			else
			{
				Mix_HookMusicFinished(NULL);
			}
			
			if(Mix_PlayMusic(gBGMMusic, loop) < 0){
				Mix_HookMusicFinished(NULL);
				return;
			}
		}
	}
	else if(endWith(fname,".wav"))
	{
		if(Mix_Playing(0)) {
			Mix_HaltChannel(0);
		}
		
		if(wave!=NULL) 
		{
			Mix_FreeChunk(wave);
			wave = NULL;
		}
		
		wave = Mix_LoadWAV(fname);
		if(wave!=NULL) {
			Mix_VolumeChunk(wave, volumLevel);
			loop = loop > 0 ? loop - 1 : loop; // 0: no loop, -1: infinite loop, n: n times of loop
			Mix_PlayChannel(0, wave, loop);
		}
	}
}

/*
 * Class:     org_recompile_mobile_Audio
 * Method:    _start
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1start(JNIEnv *aEnv, jclass cls, jstring aBgm, jint loop){
	
	
	jboolean isCopy = JNI_FALSE;
	//char *file = (char *)(aEnv->GetByteArrayElements(aBgm, &isCopy));
	const char *str = aEnv->GetStringUTFChars(aBgm, 0);
	
	changeBGM(str,loop);
	
	//aEnv->ReleaseByteArrayElements(aBgm, (jbyte*)file, JNI_ABORT);
	aEnv->ReleaseStringUTFChars(aBgm, str);
}

/*
 * Class:     org_recompile_mobile_Audio
 * Method:    _stop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1stop(JNIEnv *aEnv, jclass, jint type){
	if(type==1)//midi
	{
		if(Mix_PlayingMusic()) {
			Mix_HaltMusic();
		}
	}
	else if(type==2)//wave
	{
		if(Mix_Playing(0)) {
			Mix_HaltChannel(0);
		}
	}
}

JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1setVol
  (JNIEnv *aEnv, jclass , jint vol)
{
	if(initAudio==0)
	{
		if (SDL_Init(SDL_INIT_AUDIO) == -1){
			return;
		}

		//Mix_Init(MIX_INIT_MID);
		
		if( Mix_OpenAudio(DEFAULT_BGM_FREQ, DEFAULT_BGM_FORMAT, DEFAULT_BGM_CHAN, DEFAULT_BGM_CHUNK) < 0){
			return;
		}
		
		initAudio=1;
	}
	
	volumLevel=vol;
	
	//printf("[native] MIX_MAX_VOLUME:%d, cur:%d\n",MIX_MAX_VOLUME,vol* MIX_MAX_VOLUME / 100);
	Mix_VolumeMusic(volumLevel);
	
	//Mix_SetMusicCMD(SDL_getenv("MUSIC_CMD"));
}

JNIEXPORT void JNICALL Java_org_recompile_mobile_Audio__1destroy
  (JNIEnv *, jclass)
{
	quit();
}

/*
 * Class:     org_recompile_mobile_PlatformPlayer
 * Method:    _start
 * Signature: (Ljava/lang/String;I)V
 */
JNIEXPORT void JNICALL Java_org_recompile_mobile_PlatformPlayer__1start
  (JNIEnv *aEnv, jobject object, jstring aBgm, jint loop)
{
	if(m_jvm==NULL)
	{
		aEnv->GetJavaVM(std::addressof(m_jvm));
	}
	
	if(m_jobj!=NULL)
	{
		aEnv->DeleteGlobalRef(m_jobj);
	}
	
	m_jobj = aEnv->NewGlobalRef(object);
	if(m_onCallBack==NULL)
	{
		jclass m_jclz=aEnv->GetObjectClass(m_jobj);
		m_onCallBack = aEnv->GetMethodID(m_jclz, m_onCallBackMethod.c_str(),
									"()V");
	}
		
	const char *str = aEnv->GetStringUTFChars(aBgm, 0);
	changeBGM(str,loop);
	aEnv->ReleaseStringUTFChars(aBgm, str);
}