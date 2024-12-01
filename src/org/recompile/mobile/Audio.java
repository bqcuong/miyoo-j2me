package org.recompile.mobile;

public class Audio {
    public static void setVol(int level) {
        _setVol(level);
    }

    public static void destroy() {
        _destroy();
    }

    private native static void _setVol(int level);

    private native static void _destroy();
}