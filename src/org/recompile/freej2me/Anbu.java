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
package org.recompile.freej2me;

import android.util.Log;
import org.recompile.mobile.Audio;
import org.recompile.mobile.Mobile;
import org.recompile.mobile.MobilePlatform;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class Anbu {
    static public final String TAG = Anbu.class.getSimpleName();
    
    private static java.awt.Font globalFont = null;
    private final SDL sdl;

    private final int lcdWidth;
    private final int lcdHeight;

    private final boolean[] pressedKeys = new boolean[128];

    private final Runnable painter;
    private final SDLConfig config;
    private final byte[] keyPix = {
        //p
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        //n;
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        //e
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        //s
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
        0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        //m
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

    };
    private long pretime = 0;
    private int useFlag = 0;
    private int soundLevel = 100;
    private int fps = 16;
    private int showfps = 0;

    public Anbu(String args[]) {
        sdl = new SDL();

        if (args.length < 3) {
            System.exit(0);
        }

        lcdWidth = Integer.parseInt(args[1]);
        lcdHeight = Integer.parseInt(args[2]);

        String appname = "";
        String[] js = args[0].split("/");
        if (js.length > 0) {
            if (js[js.length - 1].endsWith(".jar")) {
                appname = js[js.length - 1].substring(0, js[js.length - 1].length() - 4);
                Log.d(TAG, "jar file name: " + appname);
            }
        }

        Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));
        Mobile.getPlatform().dataPath = "./";
        Mobile.getPlatform().rootPath = "/mnt/SDCARD/Emu/JAVA/";

        soundLevel = Integer.parseInt(args[3]);
        Audio.setVol(soundLevel);

        config = new SDLConfig();
        config.init(appname + lcdWidth + lcdHeight);
        settingsChanged();

        painter = new Runnable() {
            public void run() {
                try {
                    long last = fps - System.currentTimeMillis() - pretime;
                    if (last > 0) {
                        Thread.sleep(last);
                    }

                    pretime = System.currentTimeMillis();

                    int[] data = new int[lcdWidth * lcdHeight];
                    byte[] frame = new byte[lcdWidth * lcdHeight * 2];

                    Mobile.getPlatform().getLCD().getRGB(0, 0, lcdWidth, lcdHeight, data, 0, lcdWidth);

                    byte R, G, B;
                    short tmp;

                    for (int i = 0; i < data.length; i++) {
                        R = (byte) (data[i] >> 16);
                        G = (byte) (data[i] >> 8);
                        B = (byte) (data[i]);

                        R = (byte) (R >> 3);
                        G = (byte) (G >> 2);
                        B = (byte) (B >> 3);
                        tmp = (short) (R << 11 & 0xF800
                            | G << 5 & 0x07E0
                            | B & 0x001f);

                        frame[2 * i] = (byte) (tmp & 0x00FF);
                        frame[2 * i + 1] = (byte) ((tmp >> 8) & 0x00FF);
                    }

                    if (showfps < 60) {
                        int index = useFlag * 144;
                        int t;
                        for (int i = 0; i < 12; i++) {
                            for (int j = 0; j < 12; j++) {

                                t = ((10 + i) * lcdWidth + (10 + j)) * 2;
                                switch (keyPix[index + (i * 12) + j]) {
                                    case 0:
                                        frame[t] = (byte) 0x00;
                                        frame[t + 1] = (byte) 0x00;
                                        break;
                                    case 1:
                                        frame[t] = (byte) 0x78;
                                        frame[t + 1] = (byte) 0x0F;
                                        break;
                                }
                            }
                        }

                        showfps += 1;
                    }

                    sdl.frame.write(frame);
                    sdl.frame.flush();

                }
                catch (Exception e) {
                    Log.d(TAG, "Failed to write sdl_interface");
                    Log.d(TAG, e.getMessage());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            System.exit(0);
                        }
                    }).start();

                    Mobile.destroy();
                    Audio.destroy();
                    System.exit(0);
                }
            }
        };

        Mobile.getPlatform().setPainter(painter);
        if (Mobile.getPlatform().loadJar(args[0])) {
            sdl.start();
            Mobile.getPlatform().runJar();
        }
        else {
            Log.d(TAG, "Couldn't load jar...");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        System.loadLibrary("audio");
        Anbu app = new Anbu(args);
    }

    public static java.awt.Font getFont() {
        if (globalFont != null) {
            return globalFont;
        }
        try {
            java.awt.Font tmpFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File("./font.ttf"));
            globalFont = tmpFont.deriveFont(java.awt.Font.PLAIN, 12f);
        } catch (Exception e) {
            Log.d(TAG, "Failed to load font: " + e.getMessage());
            globalFont = new java.awt.Font("MiSans Normal", java.awt.Font.PLAIN, 12);
        }
        return globalFont;
    }

    private void settingsChanged() {
        fps = Integer.parseInt(config.settings.get("fps"));
        if (fps > 0) {
            fps = 1000 / fps;
        }

        String phone = config.settings.get("phone");
        useFlag = 0;
        if (phone.equals("n")) {
            useFlag = 1;
        } else if (phone.equals("e")) {
            useFlag = 2;
        } else if (phone.equals("s")) {
            useFlag = 3;
        } else if (phone.equals("m")) {
            useFlag = 4;
        }
    }

    private int getMobileKey(int keycode) {
        if (useFlag == 1) {
            switch (keycode) {
                case 0x40000052:
                    return Mobile.NOKIA_UP;
                case 0x40000051:
                    return Mobile.NOKIA_DOWN;
                case 0x40000050:
                    return Mobile.NOKIA_LEFT;
                case 0x4000004F:
                    return Mobile.NOKIA_RIGHT;
                case 0x0D:
                    return Mobile.NOKIA_SOFT3;

            }
        } else if (useFlag == 2) {
            switch (keycode) {
                case 0x40000052:
                    return Mobile.NOKIA_UP;
                case 0x40000051:
                    return Mobile.NOKIA_DOWN;
                case 0x40000050:
                    return Mobile.NOKIA_LEFT;
                case 0x4000004F:
                    return Mobile.NOKIA_RIGHT;
                case 0x0D:
                    return Mobile.NOKIA_SOFT3;

                case 0x30:
                    return 109;//m=0
                case 0x31:
                    return 114;//r=1
                case 0x33:
                    return 121;//y=3
                case 0x37:
                    return 118;//v=7
                case 0x39:
                    return 110;//n=9
                case 0x65:
                    return 117;//* u
                case 0x72:
                    return 106;//# j
            }
        } else if (useFlag == 3) {
            switch (keycode) {
                case 0x40000052:
                    return Mobile.SIEMENS_UP;
                case 0x40000051:
                    return Mobile.SIEMENS_DOWN;
                case 0x40000050:
                    return Mobile.SIEMENS_LEFT;
                case 0x4000004F:
                    return Mobile.SIEMENS_RIGHT;
                case 0x71:
                    return Mobile.SIEMENS_SOFT1;
                case 0x77:
                    return Mobile.SIEMENS_SOFT2;
                case 0x0D:
                    return Mobile.SIEMENS_FIRE;
            }
        } else if (useFlag == 4) {
            switch (keycode) {
                case 0x40000052:
                    return Mobile.MOTOROLA_UP;
                case 0x40000051:
                    return Mobile.MOTOROLA_DOWN;
                case 0x40000050:
                    return Mobile.MOTOROLA_LEFT;
                case 0x4000004F:
                    return Mobile.MOTOROLA_RIGHT;
                case 0x71:
                    return Mobile.MOTOROLA_SOFT1;
                case 0x77:
                    return Mobile.MOTOROLA_SOFT2;
                case 0x0D:
                    return Mobile.MOTOROLA_FIRE;
            }
        }

        switch (keycode) {
            case 0x30:
                return Mobile.KEY_NUM0;
            case 0x31:
                return Mobile.KEY_NUM1;
            case 0x32:
                return Mobile.KEY_NUM2;
            case 0x33:
                return Mobile.KEY_NUM3;
            case 0x34:
                return Mobile.KEY_NUM4;
            case 0x35:
                return Mobile.KEY_NUM5;
            case 0x36:
                return Mobile.KEY_NUM6;
            case 0x37:
                return Mobile.KEY_NUM7;
            case 0x38:
                return Mobile.KEY_NUM8;
            case 0x39:
                return Mobile.KEY_NUM9;
            case 0x2A:
                return Mobile.KEY_STAR;//*
            case 0x23:
                return Mobile.KEY_POUND;//#

            case 0x40000052:
                return Mobile.KEY_NUM2;
            case 0x40000051:
                return Mobile.KEY_NUM8;
            case 0x40000050:
                return Mobile.KEY_NUM4;
            case 0x4000004F:
                return Mobile.KEY_NUM6;

            case 0x0D:
                return Mobile.KEY_NUM5;

            case 0x71:
                return Mobile.NOKIA_SOFT1; //SDLK_q
            case 0x77:
                return Mobile.NOKIA_SOFT2;  //SDLK_w
            case 0x65:
                return Mobile.KEY_STAR;  //SDLK_e
            case 0x72:
                return Mobile.KEY_POUND;  ////SDLK_r

            case 0x64:
                if (soundLevel > 10)
                    soundLevel -= 10;
                else
                    soundLevel = 0;
                Audio.setVol(soundLevel);
                config.settings.put("sound", String.valueOf(soundLevel));
                config.saveConfig();
                break;
            case 0x75:
                if (soundLevel < 90)
                    soundLevel += 10;
                else
                    soundLevel = 100;
                Audio.setVol(soundLevel);
                config.settings.put("sound", String.valueOf(soundLevel));
                config.saveConfig();
                break;

            // Inverted Num Pad
            case 0x40000059:
                return Mobile.KEY_NUM7; // SDLK_KP_1
            case 0x4000005A:
                return Mobile.KEY_NUM8; // SDLK_KP_2
            case 0x4000005B:
                return Mobile.KEY_NUM9; // SDLK_KP_3
            case 0x4000005C:
                return Mobile.KEY_NUM4; // SDLK_KP_4
            case 0x4000005D:
                return Mobile.KEY_NUM5; // SDLK_KP_5
            case 0x4000005E:
                return Mobile.KEY_NUM6; // SDLK_KP_6
            case 0x4000005F:
                return Mobile.KEY_NUM1; // SDLK_KP_7
            case 0x40000060:
                return Mobile.KEY_NUM2; // SDLK_KP_8
            case 0x40000061:
                return Mobile.KEY_NUM3; // SDLK_KP_9
            case 0x40000062:
                return Mobile.KEY_NUM0; // SDLK_KP_0

            case 0x63://c
                useFlag = (useFlag + 1) % 5;
                if (useFlag == 0) {
                    config.settings.put("phone", "p");
                } else if (useFlag == 1) {
                    config.settings.put("phone", "n");
                } else if (useFlag == 2) {
                    config.settings.put("phone", "e");
                } else if (useFlag == 3) {
                    config.settings.put("phone", "s");
                } else if (useFlag == 4) {
                    config.settings.put("phone", "m");
                }
                showfps = 0;
                config.saveConfig();

                break;

            // F4 - Quit
            case -1:
                Mobile.destroy();
                Audio.destroy();
                sdl.stop();
                System.exit(0);
                break;

            // ESC - Quit
            case 0x1B:
                Mobile.destroy();
                Audio.destroy();
                sdl.stop();
                System.exit(0);
                break;

            // HOME - Quit
            case 0x4000004a:
                Mobile.destroy();
                Audio.destroy();
                sdl.stop();
                System.exit(0);
                break;
        }
        return 0;
    }

    private class SDL {
        public OutputStream frame;
        private Timer keytimer;
        private TimerTask keytask;
        private Process proc;
        private InputStream keys;

        public void start() {
            try {
                String[] args = {"./sdl_interface", String.valueOf(lcdWidth), String.valueOf(lcdHeight)};

                ProcessBuilder processBuilder = new ProcessBuilder(args);
                proc = processBuilder.start();
                keys = proc.getInputStream(); //  miyoo mini/x64-linux
                frame = proc.getOutputStream();

                keytimer = new Timer();
                keytask = new SDLKeyTimerTask();
                keytimer.schedule(keytask, 0, 5);

            } catch (Exception e) {
                Log.d(TAG, "Failed to start sdl_interface");
                Log.d(TAG, e.getMessage());
                System.exit(0);
            }
        }

        public void stop() {
            keytimer.cancel();
            proc.destroy();
        }

        private class SDLKeyTimerTask extends TimerTask {
            private int bin;
            private byte[] din = new byte[6];
            private int count = 0;
            private int code = 0;
            private int mobikey;
            private int mobikeyN;
            private int x, y;
            private boolean press = false;

            public void run() {
                try // to read keys
                {
                    while (true) {
                        bin = keys.read();
                        if (bin == -1) {
                            return;
                        }
                        din[count] = (byte) (bin & 0xFF);
                        count++;
                        if (count == 5) {
                            count = 0;

                            switch (din[0] >>> 4) {
                                case 0:
                                    code = (din[1] << 24) | (din[2] << 16) | (din[3] << 8) | din[4];
                                    mobikey = getMobileKey(code);
                                    break;
                                case 1:
                                    x = ((din[1] << 8) & 0xFF00) | (din[2] & 0x00FF);
                                    y = ((din[3] << 8) & 0xFF00) | (din[4] & 0x00FF);

                                    if (din[0] % 2 == 0) {
                                        Mobile.getPlatform().pointerReleased(x, y);
                                        press = false;
                                    } else {
                                        if (press)
                                            return;
                                        Mobile.getPlatform().pointerPressed(x, y);
                                        press = true;
                                    }
                                    return;
                                default:
                                    continue;
                            }

                            if (mobikey == 0) //Ignore events from keys not mapped to a phone keypad key
                            {
                                return;
                            }

                            mobikeyN = (mobikey + 64) & 0x7F; //Normalized value for indexing the pressedKeys array

                            if (din[0] % 2 == 0) {
                                //Key released
                                Mobile.getPlatform().keyReleased(mobikey);
                                pressedKeys[mobikeyN] = false;
                            } else {
                                //Key pressed or repeated
                                if (pressedKeys[mobikeyN] == false) {
                                    //Log.d(TAG, "keyPressed:  " + Integer.toString(mobikey));
                                    Mobile.getPlatform().keyPressed(mobikey);
                                } else {
                                    //Log.d(TAG, "keyRepeated:  " + Integer.toString(mobikey));
                                    Mobile.getPlatform().keyRepeated(mobikey);
                                }
                                pressedKeys[mobikeyN] = true;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
}
