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
*/
package javax.microedition.media;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

import android.util.Log;
import org.recompile.mobile.PlatformPlayer;

import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

public final class Manager
{
	static public final String TAG = Manager.class.getSimpleName();

	public static final String TONE_DEVICE_LOCATOR = "device://tone";

	public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException
	{
		return new PlatformPlayer(stream, type);
	}

	public static Player createPlayer(String locator) throws MediaException
	{
		Log.d(TAG, "Create Player: " + locator);
		return new PlatformPlayer(locator);
	}
	
	public static Player createPlayer(DataSource source) throws IOException, MediaException {
		if (source == null) {
			throw new IllegalArgumentException("Null datasource for creating PlatformPlayer");
		}

		String type = source.getContentType();
		Log.d(TAG, "Create PlatformPlayer from datasource: " + type);
		String[] supportedTypes = getSupportedContentTypes(null);
		
		for (int i = 0; i < supportedTypes.length; i++) {
			if(type != null && supportedTypes[i].equals(type.toLowerCase()))
			{
				source.connect();
				SourceStream[] sourceStreams = source.getStreams();
				if (sourceStreams == null || sourceStreams.length == 0) {
					throw new MediaException();
				}
				SourceStream sourceStream = sourceStreams[0];
				InputStream stream = new InternalSourceStream(sourceStream);
				return new PlatformPlayer(stream, type);
			}
		}
		
		throw new MediaException();
	}
	
	public static String[] getSupportedContentTypes(String protocol)
	{
		return new String[]{"audio/midi", "audio/x-wav", "sp-midi", "audio/spmidi",
		"audio/amr", "audio/mpeg"};
	}
	
	public static String[] getSupportedProtocols(String content_type)
	{
		Log.d(TAG, "Get Supported Media Protocols");
		return new String[]{"device", "file", "http"};
	}
	
	public static void playTone(int note, int duration, int volume)
	{
		Log.d(TAG, "Play Tone");
	}

}
