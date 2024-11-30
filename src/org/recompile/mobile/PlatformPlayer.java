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
package org.recompile.mobile;

import android.util.Log;

import javax.microedition.media.Control;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Vector;

public class PlatformPlayer implements Player {
    static public final String TAG = PlatformPlayer.class.getSimpleName();

    private String contentType = "";

    private audioplayer player;

    private int state = Player.UNREALIZED;

    private Vector<PlayerListener> listeners;

    private Control[] controls;

    public PlatformPlayer(InputStream stream, String type) {
        listeners = new Vector<>();
        controls = new Control[3];
        contentType = type;

        Log.d(TAG, "media type: " + type);

        if (!Mobile.sound) {
            player = new audioplayer();
        }
        else {
            if (type.equalsIgnoreCase("audio/mid") || type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("sp-midi")
                || type.equalsIgnoreCase("audio/spmidi")) {
                player = new midiPlayer(stream, ".mid");
            }
            else if (type.equalsIgnoreCase("audio/mpeg") || type.equalsIgnoreCase("audio/x-wav") || type.equalsIgnoreCase("audio/wav")) {
                player = new midiPlayer(stream, ".wav");
            }
            else { /* TODO: Implement a player for amr and mpeg audio types */
                Log.d(TAG, "No Player For: " + contentType);
                player = new audioplayer();
            }
        }
        controls[0] = new volumeControl();
        controls[1] = new tempoControl();
        controls[2] = new midiControl();
    }

    public PlatformPlayer(String locator) {
        player = new audioplayer();
        listeners = new Vector<>();
        controls = new Control[3];
        Log.d(TAG, "Player locator: " + locator);
    }

    public String encodeHexString(byte[] data, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02x", data[i]));
        }
        return sb.toString();
    }

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


    public void close() {
        try {
            player.stop();
            state = Player.CLOSED;
            notifyListeners(PlayerListener.CLOSED, null);
        } catch (Exception e) {
        }
        state = Player.CLOSED;
    }

    public int getState() {
        return state;
    }

    public void start() {
        try {
            player.start();
        } catch (Exception e) {
        }
    }

    public void stop() {
        try {
            player.stop();
        } catch (Exception e) {
        }
    }

    public void addPlayerListener(PlayerListener playerListener) {
        listeners.add(playerListener);
    }

    public void removePlayerListener(PlayerListener playerListener) {
        listeners.remove(playerListener);
    }

    private void notifyListeners(String event, Object eventData) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).playerUpdate(this, event, eventData);
        }
    }

    public void deallocate() {
        stop();
        player.deallocate();
        notifyListeners(PlayerListener.END_OF_MEDIA, 0);
        state = Player.UNREALIZED;
    }

    public String getContentType() {
        return contentType;
    }

    public long getDuration() {
        return Player.TIME_UNKNOWN;
    }

    public long getMediaTime() {
        return player.getMediaTime();
    }

    public void prefetch() {
        state = Player.PREFETCHED;
    }

    public void realize() {
        state = Player.REALIZED;
    }

    public void setLoopCount(int count) {
        player.setLoopCount(count);
    }

    public long setMediaTime(long now) {
        return player.setMediaTime(now);
    }

    public Control getControl(String controlType) {
        if (controlType.equals("VolumeControl")) {
            return controls[0];
        }
        if (controlType.equals("TempoControl")) {
            return controls[1];
        }
        if (controlType.equals("MIDIControl")) {
            return controls[2];
        }
        if (controlType.equals("javax.microedition.media.control.VolumeControl")) {
            return controls[0];
        }
        if (controlType.equals("javax.microedition.media.control.TempoControl")) {
            return controls[1];
        }
        if (controlType.equals("javax.microedition.media.control.MIDIControl")) {
            return controls[2];
        }
        return null;
    }

    public Control[] getControls() {
        return controls;
    }

    public void musicFinish() {
        notifyListeners(PlayerListener.END_OF_MEDIA, 0);
    }

    private class audioplayer {
        public void start() {
        }

        public void stop() {
        }

        public void setLoopCount(int count) {
        }

        public long setMediaTime(long now) {
            return now;
        }

        public long getMediaTime() {
            return 0;
        }

        public boolean isRunning() {
            return false;
        }

        public void deallocate() {
        }
    }

    private class midiPlayer extends audioplayer {
        private int loops = 1;
        private boolean isrun = false;
        private String bgmFileName = "";

        public midiPlayer(InputStream stream, String type) {
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

                bgmFileName = filename + type;
                File file = new File(bgmFileName);

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
            } catch (Exception e) {
                Log.d(TAG, "Error saving rms file: " + e.getMessage());
            }
        }

        public void start() {
            if (bgmFileName.equals("")) {
                return;
            }

            if (isRunning() && bgmFileName.endsWith(".mid")) {
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

                byte[] fname = bgmFileName.getBytes("UTF-8");
                for (int i = 0; i < fname.length; i++) {
                    frame[i + 6] = fname[i];
                }

                Audio.start(bgmFileName, loops);
            }
            catch (Exception e) {
                Log.d(TAG, "Error starting sound: " + e.getMessage());
            }
            isrun = true;
            state = Player.STARTED;
        }

        public void stop() {
            if (!isRunning()) return;
            try {
                byte[] frame = new byte[100];
                frame[0] = '$';
                frame[1] = 'S';

                if (bgmFileName.endsWith(".mid")) {
                    Audio.stop(1);
                }
                else if (bgmFileName.endsWith(".wav")) {
                    Audio.stop(2);
                }
            }
            catch (Exception e) {
                Log.d(TAG, "Error stopping sound: " + e.getMessage());
            }

            isrun = false;

            state = Player.PREFETCHED;
        }

        public void deallocate() {
        }

        public void setLoopCount(int count) {
            loops = count;
        }

        public long setMediaTime(long now) {
            return now;
        }

        public long getMediaTime() {
            return 0;
        }

        public boolean isRunning() {
            return isrun;
        }
    }

    private class wavPlayer extends audioplayer {

        private int loops = 0;
        private boolean isrun = false;

        public wavPlayer(InputStream stream) {
            state = Player.PREFETCHED;
        }

        public void start() {
            if (isRunning()) {
                return;
            }
            state = Player.STARTED;
            isrun = true;
        }

        public void stop() {
            state = Player.PREFETCHED;
            isrun = false;
        }

        public void setLoopCount(int count) {
            loops = count;
        }

        public long setMediaTime(long now) {
            return now;
        }

        public long getMediaTime() {
            return 0;
        }

        public boolean isRunning() {
            return isrun;
        }
    }

    private class midiControl implements javax.microedition.media.control.MIDIControl {
        public int[] getBankList(boolean custom) {
            return new int[]{};
        }

        public int getChannelVolume(int channel) {
            return 0;
        }

        public java.lang.String getKeyName(int bank, int prog, int key) {
            return "";
        }

        public int[] getProgram(int channel) {
            return new int[]{};
        }

        public int[] getProgramList(int bank) {
            return new int[]{};
        }

        public java.lang.String getProgramName(int bank, int prog) {
            return "";
        }

        public boolean isBankQuerySupported() {
            return false;
        }

        public int longMidiEvent(byte[] data, int offset, int length) {
            return 0;
        }

        public void setChannelVolume(int channel, int volume) {
        }

        public void setProgram(int channel, int bank, int program) {
        }

        public void shortMidiEvent(int type, int data1, int data2) {
        }
    }

    private class volumeControl implements javax.microedition.media.control.VolumeControl {
        private int level = 100;
        private boolean muted = false;

        public int getLevel() {
            return level;
        }

        public boolean isMuted() {
            return muted;
        }

        public int setLevel(int value) {
            level = value;
            return level;
        }

        public void setMute(boolean mute) {
            muted = mute;
        }
    }

    private class tempoControl implements javax.microedition.media.control.TempoControl {
        int tempo = 5000;
        int rate = 5000;

        public int getTempo() {
            return tempo;
        }

        public int setTempo(int millitempo) {
            tempo = millitempo;
            return tempo;
        }

        // RateControl interface
        public int getMaxRate() {
            return rate;
        }

        public int getMinRate() {
            return rate;
        }

        public int getRate() {
            return rate;
        }

        public int setRate(int millirate) {
            rate = millirate;
            return rate;
        }
    }
}
