package org.recompile.mobile;

import android.util.Log;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SDLMixerPlayer extends AudioPlayer {
    static public final String TAG = SDLMixerPlayer.class.getSimpleName();

    private final PlatformPlayer platformPlayer;
    private String savedFile = "";

    public SDLMixerPlayer(PlatformPlayer platformPlayer, InputStream stream, String type) {
        this.platformPlayer = platformPlayer;

        try {
            String rmsPath = "./rms/" + Mobile.getPlatform().loader.suitename;
            try {
                Files.createDirectories(Paths.get(rmsPath));
            }
            catch (Exception e) {
                Log.d(TAG, "Error create game rms folder:" + e.getMessage());
            }
            byte[] buffer = new byte[1024];
            int len;
            String filename = "";
            if ((len = stream.read(buffer)) != -1) {
                filename = encodeMD5String(buffer);
                filename = "./rms/" + Mobile.getPlatform().loader.suitename + "/" + filename;
            }

            savedFile = filename + type;
            File file = new File(savedFile);

            if (!file.exists()) {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(buffer, 0, len);
                    while ((len = stream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                catch (Exception e) {
                    Log.d(TAG, "Error saving file: " + e.getMessage());
                }
            }

            if (type.equals(".amr")) {
                savedFile = savedFile.replace(".amr", ".wav");
                File wavFile = new File(savedFile);

                // convert amr to flac (disguised with wav extension)
                if (!wavFile.exists()) {
                    Log.d(TAG, "Converting amr to flac (disguised as wav)");
                    boolean res = convertAmr2Flac(filename);
                    if (!res) Log.e(TAG, "Conversion didn't succeed!");
                }
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Error saving rms file: " + e.getMessage());
        }
    }

    private boolean convertAmr2Flac(String filename) {
        try {
            String[] command = new String[] { "sh", "./ffmpeg.sh", filename };
            Runtime.getRuntime().exec(command);
            File wavFile = new File(filename + ".wav");
            return wavFile.exists();
        }
        catch (Exception e) {
            Log.d(TAG, "Error converting file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void start() {
        if (savedFile.isEmpty()) {
            return;
        }

        try {
            byte[] frame = new byte[100];
            frame[0] = '$';
            frame[1] = 'C';

            frame[2] = (byte) (loops);
            frame[3] = (byte) (loops >> 8);
            frame[4] = (byte) (loops >> 16);
            frame[5] = (byte) (loops >> 24);

            byte[] fname = savedFile.getBytes("UTF-8");
            for (int i = 0; i < fname.length; i++) {
                frame[i + 6] = fname[i];
            }

            this.platformPlayer._start(savedFile, loops);
        }
        catch (Exception e) {
            Log.d(TAG, "Error starting sound: " + e.getMessage());
        }
        running = true;
        platformPlayer.setState(Player.STARTED);
        platformPlayer.notifyListeners(PlayerListener.STARTED, getMediaTime());
    }

    @Override
    public void stop() {
        if (!isRunning()) return;
        try {
            byte[] frame = new byte[100];
            frame[0] = '$';
            frame[1] = 'S';

            if (savedFile.endsWith(".mid")) {
                platformPlayer._stop(1);
            }
            else if (savedFile.endsWith(".wav")) {
                platformPlayer._stop(2);
            }
        }
        catch (Exception e) {
            Log.d(TAG, "Error stopping sound: " + e.getMessage());
        }

        running = false;
        platformPlayer.setState(Player.PREFETCHED);
        platformPlayer.notifyListeners(PlayerListener.STARTED, getMediaTime());
    }

    @Override
    public void deallocate() {
        // Prefetch does "nothing" in each internal player so deallocate must also do nothing
    }
}
