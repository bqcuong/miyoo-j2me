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

    private AudioPlayer player;

    private int state = Player.UNREALIZED;

    private Vector<PlayerListener> listeners;

    private Control[] controls;

    public PlatformPlayer(InputStream stream, String type) {
        listeners = new Vector<>();
        controls = new Control[3];
        contentType = type;

        if (!Mobile.sound) {
            player = new FakeAudioPlayer();
        }
        else {
            if (type.equalsIgnoreCase("audio/mid") || type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("sp-midi")
                || type.equalsIgnoreCase("audio/spmidi")) {
                player = new SDLMixerPlayer(stream, ".mid");
            }
            else if (type.equalsIgnoreCase("audio/mpeg") || type.equalsIgnoreCase("audio/x-wav") || type.equalsIgnoreCase("audio/wav")) {
                player = new SDLMixerPlayer(stream, ".wav");
            }
            else {
                Log.d(TAG, "No Player For: " + contentType);
                player = new FakeAudioPlayer();
            }
        }
        controls[0] = new VolumeControl();
        controls[1] = new TempoControl();
        controls[2] = new MIDIControl();
    }

    public PlatformPlayer(String locator) {
        player = new FakeAudioPlayer();
        listeners = new Vector<>();
        controls = new Control[3];
        Log.d(TAG, "Player locator: " + locator);
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

    @Override
    public int getState() {
        Log.d(TAG, "Player getState: " + state);
        return state;
    }

    @Override
    public void realize() {
        if (this.state == Player.CLOSED) {
            throw new IllegalStateException("Cannot realize player, as it is in the CLOSED state");
        }

        if (this.state == Player.UNREALIZED) {
            state = Player.REALIZED;
        }
    }

    @Override
    public void prefetch() {
        if (this.state == Player.CLOSED) {
            throw new IllegalStateException("Cannot prefetch player, as it is in the CLOSED state");
        }

        if (this.state == Player.UNREALIZED) {
            realize();
        }

        if (this.state == Player.REALIZED) {
            state = Player.PREFETCHED;
        }
    }

    @Override
    public void start() {
        if (this.state == Player.CLOSED) {
            throw new IllegalStateException("Cannot start player, as it is in the CLOSED state");
        }

        try {
            if (this.state == Player.REALIZED || this.state == Player.UNREALIZED) {
                prefetch();
            }
            if (this.state == Player.PREFETCHED) {
                player.start();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot start player: ", e);
        }
    }

    @Override
    public void stop() {
        if (this.state == Player.CLOSED) {
            throw new IllegalStateException("Cannot call stop() on a CLOSED player.");
        }

        try {
            if (this.state == Player.STARTED) {
                player.stop();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot stop player: ", e);
        }
    }

    @Override
    public void close() {
        if (this.state == Player.CLOSED) {
            return;
        }

        try {
            if (player.isRunning()) {
                stop();
            }
            player.stop();
            player = null;
            state = Player.CLOSED;
            notifyListeners(PlayerListener.CLOSED, null);
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot close player: ", e);
        }
    }

    @Override
    public void deallocate() {
        if (this.state == Player.CLOSED) { throw new IllegalStateException("Cannot deallocate player, it is already CLOSED."); }

        if (player.isRunning()) {
            stop();
        }

        player.deallocate();
        notifyListeners(PlayerListener.END_OF_MEDIA, 0);

        if (state > Player.UNREALIZED) {
            state = Player.UNREALIZED;
        }
    }

    @Override
    public void addPlayerListener(PlayerListener playerListener) {
        listeners.add(playerListener);
    }

    @Override
    public void removePlayerListener(PlayerListener playerListener) {
        listeners.remove(playerListener);
    }

    private void notifyListeners(String event, Object eventData) {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).playerUpdate(this, event, eventData);
        }
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getDuration() {
        return Player.TIME_UNKNOWN;
    }

    @Override
    public long getMediaTime() {
        return player.getMediaTime();
    }

    @Override
    public void setLoopCount(int count) {
        player.setLoopCount(count);
    }

    @Override
    public long setMediaTime(long now) {
        return player.setMediaTime(now);
    }

    @Override
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

    @Override
    public Control[] getControls() {
        return controls;
    }

    // Will be invoked by libaudio
    public void musicFinish() {
        Log.d(TAG, "musicFinish() is called!");
        state = Player.PREFETCHED;
        notifyListeners(PlayerListener.END_OF_MEDIA, 0);
    }

    private static abstract class AudioPlayer {
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
    }

    private static class FakeAudioPlayer extends AudioPlayer {
        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void deallocate() {

        }
    }

    private class SDLMixerPlayer extends AudioPlayer {
        private String savedFile = "";

        public SDLMixerPlayer(InputStream stream, String type) {
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
            }
            catch (Exception e) {
                Log.d(TAG, "Error saving rms file: " + e.getMessage());
            }
        }

        @Override
        public void start() {
            if (savedFile.isEmpty()) {
                return;
            }

            if (isRunning() && savedFile.endsWith(".mid")) {
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

                Audio.start(savedFile, loops);
            }
            catch (Exception e) {
                Log.d(TAG, "Error starting sound: " + e.getMessage());
            }
            running = true;
            state = Player.STARTED;
            notifyListeners(PlayerListener.STARTED, getMediaTime());
        }

        @Override
        public void stop() {
            if (!isRunning()) return;
            try {
                byte[] frame = new byte[100];
                frame[0] = '$';
                frame[1] = 'S';

                if (savedFile.endsWith(".mid")) {
                    Audio.stop(1);
                } else if (savedFile.endsWith(".wav")) {
                    Audio.stop(2);
                }
            }
            catch (Exception e) {
                Log.d(TAG, "Error stopping sound: " + e.getMessage());
            }

            running = false;
            state = Player.PREFETCHED;
            notifyListeners(PlayerListener.STARTED, getMediaTime());
        }

        @Override
        public void deallocate() {
            // Prefetch does "nothing" in each internal player so deallocate must also do nothing
        }
    }

    private static class MIDIControl implements javax.microedition.media.control.MIDIControl {

        @Override
        public int[] getBankList(boolean custom) {
            return new int[]{};
        }

        @Override
        public int getChannelVolume(int channel) {
            return 0;
        }

        @Override
        public java.lang.String getKeyName(int bank, int prog, int key) {
            return "";
        }

        @Override
        public int[] getProgram(int channel) {
            return new int[]{};
        }

        @Override
        public int[] getProgramList(int bank) {
            return new int[]{};
        }

        @Override
        public String getProgramName(int bank, int prog) {
            return "";
        }

        @Override
        public boolean isBankQuerySupported() {
            return false;
        }

        @Override
        public int longMidiEvent(byte[] data, int offset, int length) {
            return 0;
        }

        @Override
        public void setChannelVolume(int channel, int volume) {
        }

        @Override
        public void setProgram(int channel, int bank, int program) {
        }

        @Override
        public void shortMidiEvent(int type, int data1, int data2) {
        }
    }

    private static class VolumeControl implements javax.microedition.media.control.VolumeControl {
        private int level = 100;
        private boolean muted = false;

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public boolean isMuted() {
            return muted;
        }

        @Override
        public int setLevel(int value) {
            level = value;
            return level;
        }

        @Override
        public void setMute(boolean mute) {
            muted = mute;
        }
    }

    private static class TempoControl implements javax.microedition.media.control.TempoControl {
        int tempo = 5000;
        int rate = 5000;

        @Override
        public int getTempo() {
            return tempo;
        }

        @Override
        public int setTempo(int milliTempo) {
            tempo = milliTempo;
            return tempo;
        }

        // RateControl interface
        @Override
        public int getMaxRate() {
            return rate;
        }

        @Override
        public int getMinRate() {
            return rate;
        }

        @Override
        public int getRate() {
            return rate;
        }

        @Override
        public int setRate(int milliRate) {
            rate = milliRate;
            return rate;
        }
    }
}
