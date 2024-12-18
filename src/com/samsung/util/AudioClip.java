
/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/package com.samsung.util;

import android.util.Log;

public class AudioClip
{
	static public final String TAG = AudioClip.class.getSimpleName();
	
	public static final int TYPE_MMF = 1;
	public static final int TYPE_MP3 = 2;
	public static final int TYPE_MIDI = 3;

	private int type;

	public AudioClip(int clipType, byte[] audioData, int audioOffset, int audioLength)
	{
		Log.d(TAG, "Samsung AudioClip");
		type = clipType;
	}

	public AudioClip(int clipType, String filename)
	{
		Log.d(TAG, "Samsung AudioClip");
		type = clipType;
	}

	public static boolean isSupported()
	{
		return false;
	}

	public void pause() {  }

	public void play(int loop, int volume) {  }

	public void resume() {  }

	public void stop() {  }

}
