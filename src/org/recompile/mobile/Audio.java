package org.recompile.mobile;

public class Audio {
    static public void start(String soundFile, int loop) {
        _start(soundFile, loop);
    }

    static public void stop(int type) {
        _stop(type);
    }

    static public void setVol(int level) {
        _setVol(level);
    }

    static public void destroy() {
        _destroy();
    }

    public void onCallback(String message) {
        System.out.println("Callback received in class Audio: " + message);
    }

    private native static void _start(String soundFile, int loop);

    private native static void _stop(int type);

    private native static void _setVol(int level);

    private native static void _destroy();

}