/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.demo.egl;

import android.util.Log;
import org.lwjgl.egl.EGL;
import org.lwjgl.egl.EGLCapabilities;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Field;
import java.nio.IntBuffer;

import static org.lwjgl.egl.EGL14.*;
import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class EGLDemo {
    static public final String TAG = EGLDemo.class.getSimpleName();

    public EGLDemo() {
    }

    public static void main(String[] args) {
    

        int WIDTH  = 300;
        int HEIGHT = 300;



        // EGL capabilities
        long dpy = eglGetDisplay(EGL_DEFAULT_DISPLAY);

        EGLCapabilities egl;
        try (MemoryStack stack = stackPush()) {
            IntBuffer major = stack.mallocInt(1);
            IntBuffer minor = stack.mallocInt(1);

            if (!eglInitialize(dpy, major, minor)) {
                throw new IllegalStateException(String.format("Failed to initialize EGL [0x%X]", eglGetError()));
            }

            egl = EGL.createDisplayCapabilities(dpy, major.get(0), minor.get(0));
        }

        try {
            Log.d(TAG, "EGL Capabilities:");
            for (Field f : EGLCapabilities.class.getFields()) {
                if (f.getType() == boolean.class) {
                    if (f.get(egl).equals(Boolean.TRUE)) {
                        Log.d(TAG, "\t" + f.getName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        GLESCapabilities gles = GLES.createCapabilities();

        try {
            Log.d(TAG, "OpenGL ES Capabilities:");
            for (Field f : GLESCapabilities.class.getFields()) {
                if (f.getType() == boolean.class) {
                    if (f.get(gles).equals(Boolean.TRUE)) {
                        Log.d(TAG, "\t" + f.getName());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "GL_VENDOR: " + glGetString(GL_VENDOR));
        Log.d(TAG, "GL_VERSION: " + glGetString(GL_VERSION));
        Log.d(TAG, "GL_RENDERER: " + glGetString(GL_RENDERER));
		
		Log.d(TAG, "GL_error: " +glGetError());


        glClearColor(0.0f, 0.5f, 1.0f, 0.0f);
        

        GLES.setCapabilities(null);

    }

}