package org.recompile.mobile;

import android.util.Log;

import java.security.MessageDigest;

public abstract class AudioPlayer {
    static public final String TAG = AudioPlayer.class.getSimpleName();

    protected long mediaTime = 0;
    protected int loops = 1;
    protected boolean running = false;

    public abstract void start();

    public abstract void stop();

    public void setLoopCount(int count) {
        loops = count;
    }

    public long setMediaTime(long now) {
        mediaTime = now;
        return mediaTime;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public boolean isRunning() {
        return running;
    }

    public abstract void deallocate();

    final public String encodeMD5String(byte[] data) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("MD5");
            byte[] hashed = sha256.digest(data);

            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error encodeMD5String:" + e.getMessage());
        }
        return sb.toString();
    }
}