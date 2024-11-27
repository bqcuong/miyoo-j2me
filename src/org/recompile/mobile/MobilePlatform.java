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

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import java.awt.image.BufferedImage;
import java.net.URL;

public class MobilePlatform {

    public int lcdWidth;
    public int lcdHeight;
    public MIDletLoader loader;
    public Runnable painter;
    public String dataPath = "";
    public String rootPath = "";
    public boolean suppressKeyEvents = false;
    private PlatformImage lcd;
    private PlatformGraphics gc;
    private int keyState = 0;
    private int[] keyStateArr = new int[6];
    private int s = 0;
    private int e = 0;

    public MobilePlatform(int width, int height) {
        lcdWidth = width;
        lcdHeight = height;

        lcd = new PlatformImage(width, height);
        gc = lcd.getGraphics();

        painter = new Runnable() {
            public void run() {
                // Placeholder
            }
        };
    }

    public void push(int state) {
        if ((e + 1) % 6 == s) {
            return;
        }

        keyStateArr[e] = state;
        e = (e + 1) % 6;

    }

    public int pop() {
        if (s == e) {
            return 0;
        }
        int state = keyStateArr[s];
        s = (s + 1) % 6;
        return state;

    }

    public void resizeLCD(int width, int height) {
        lcdWidth = width;
        lcdHeight = height;

        lcd = new PlatformImage(width, height);
        gc = lcd.getGraphics();
    }

    public BufferedImage getLCD() {
        return lcd.getCanvas();
    }

    public void setPainter(Runnable r) {
        painter = r;
    }

    public void keyPressed(int keycode) {
        updateKeyState(keycode, 1);
        if (!suppressKeyEvents)
            Mobile.getDisplay().getCurrent().keyPressed(keycode);
    }

    public void keyReleased(int keycode) {
        updateKeyState(keycode, 0);
        if (!suppressKeyEvents)
            Mobile.getDisplay().getCurrent().keyReleased(keycode);

    }

    public void keyRepeated(int keycode) {
        if (!suppressKeyEvents)
            Mobile.getDisplay().getCurrent().keyRepeated(keycode);

    }

    public void pointerDragged(int x, int y) {
        Mobile.getDisplay().getCurrent().pointerDragged(x, y);
    }

    public void pointerPressed(int x, int y) {
        Mobile.getDisplay().getCurrent().pointerPressed(x, y);
    }

    public void pointerReleased(int x, int y) {
        Mobile.getDisplay().getCurrent().pointerReleased(x, y);
    }


    public int getKeyState() {

        int ks = 0;

        synchronized (this) {
            ks = keyState;
        }

        return ks;
    }

    private void updateKeyState(int key, int val) {
        int mask = 0;
        switch (key) {
            case Mobile.KEY_NUM2:
                mask = GameCanvas.UP_PRESSED;
                break;
            case Mobile.KEY_NUM4:
                mask = GameCanvas.LEFT_PRESSED;
                break;
            case Mobile.KEY_NUM6:
                mask = GameCanvas.RIGHT_PRESSED;
                break;
            case Mobile.KEY_NUM8:
                mask = GameCanvas.DOWN_PRESSED;
                break;
            case Mobile.KEY_NUM5:
            case Mobile.NOKIA_SOFT3:
                mask = GameCanvas.FIRE_PRESSED;
                break;
            case Mobile.KEY_NUM7:
                mask = GameCanvas.GAME_A_PRESSED;
                break;
            case Mobile.KEY_NUM9:
                mask = GameCanvas.GAME_B_PRESSED;
                break;
            case Mobile.KEY_STAR:
                mask = GameCanvas.GAME_C_PRESSED;
                break;
            case Mobile.KEY_POUND:
                mask = GameCanvas.GAME_D_PRESSED;
                break;
            case Mobile.NOKIA_UP:
                mask = GameCanvas.UP_PRESSED;
                break;
            case Mobile.NOKIA_LEFT:
                mask = GameCanvas.LEFT_PRESSED;
                break;
            case Mobile.NOKIA_RIGHT:
                mask = GameCanvas.RIGHT_PRESSED;
                break;
            case Mobile.NOKIA_DOWN:
                mask = GameCanvas.DOWN_PRESSED;
                break;
        }

        if (mask == 0) {
            return;
        }

        synchronized (this) {
            if (val == 1) {
                keyState |= mask;
            } else {
                keyState &= ~mask;
            }
        }
    }

    private int convertGameKeyCode(int keyCode) {
        switch (keyCode) {
            case Mobile.NOKIA_LEFT:
            case Mobile.KEY_NUM4:
                return GameCanvas.LEFT_PRESSED;
            case Mobile.NOKIA_UP:
            case Mobile.KEY_NUM2:
                return GameCanvas.UP_PRESSED;
            case Mobile.NOKIA_RIGHT:
            case Mobile.KEY_NUM6:
                return GameCanvas.RIGHT_PRESSED;
            case Mobile.NOKIA_DOWN:
            case Mobile.KEY_NUM8:
                return GameCanvas.DOWN_PRESSED;
            case Mobile.NOKIA_SOFT3:
            case Mobile.KEY_NUM5:
                return GameCanvas.FIRE_PRESSED;
            case Mobile.KEY_NUM7:
                return GameCanvas.GAME_A_PRESSED;
            case Mobile.KEY_NUM9:
                return GameCanvas.GAME_B_PRESSED;
            case Mobile.KEY_STAR:
                return GameCanvas.GAME_C_PRESSED;
            case Mobile.KEY_POUND:
                return GameCanvas.GAME_D_PRESSED;
            default:
                return 0;
        }
    }

    public boolean loadJar(String jarurl) {
        try {
            URL jar;
            if (jarurl.startsWith("/")) {
                jar = new URL("file:" + jarurl);
            } else {
                jar = new URL(jarurl);
            }

            System.out.println("[jar file url] " + jar);

            String appname = "";
            String[] js = jarurl.split("/");
            if (js.length > 0) {
                if (js[js.length - 1].endsWith(".jar")) {
                    appname = js[js.length - 1].substring(0, js[js.length - 1].length() - 4);
                    System.out.println("jar file name:" + appname);
                }
            }

            loader = new MIDletLoader(new URL[]{jar}, jarurl, appname + lcdWidth + lcdHeight);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }

    }


    public void runJar() {
        try {
            loader.start();
        } catch (Exception e) {
            System.out.println("Error Running Jar");
            e.printStackTrace();
        }
    }

    public void flushGraphics(Image img, int x, int y, int width, int height) {
        gc.flushGraphics(img, x, y, width, height);

        painter.run();
    }

    public void repaint(Image img, int x, int y, int width, int height) {
        gc.flushGraphics(img, x, y, width, height);

        painter.run();

    }

}
