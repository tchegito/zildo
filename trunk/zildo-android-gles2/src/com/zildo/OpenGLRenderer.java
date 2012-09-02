/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.zildo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.client.stage.GameStage;
import zildo.fwk.ZUtils;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.platform.opengl.AndroidPixelShaders;
import zildo.platform.opengl.utils.GLUtils;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class OpenGLRenderer implements Renderer {
	
	Client client;
	TouchListener touchListener;
	GameStage game;
	
	boolean initialized = false;
	
	public OpenGLRenderer(Client client, TouchListener touchListener) {
		this.client = client;
		this.touchListener = touchListener;
	}
	
	public void resume() {
	}
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0f, 1.0f);
		//GLES20.glShadeModel(GL11.GL_SMOOTH);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDisable(GL11.GL_DEPTH_TEST);
		//gl.glDepthFunc(GL11.GL_LEQUAL);
		//GLES20.glHint(GLES20.GL_PERSPECTIVE_CORRECTION_HINT,
        //        GLES20.GL_NICEST);


		GLES20.glDisable(GL11.GL_CULL_FACE);

        //ByteBuffer temp = ByteBuffer.allocateDirect(16);
        //temp.order(ByteOrder.nativeOrder());
        /*
        gl.glLightfv(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) temp
                        .asFloatBuffer().put(lightAmbient).flip()); // Setup The Ambient
                                                                                                                // Light
        gl.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) temp
                        .asFloatBuffer().put(lightDiffuse).flip()); // Setup The Diffuse
                                                                                                                // Light
        gl.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) temp
                        .asFloatBuffer().put(lightPosition).flip()); // Position The
                                                                                                                        // Light
        gl.glEnable(GL11.GL_LIGHT1); // Enable Light One
        */


        //Display.setVSyncEnabled(true);

        
		Log.d("openglrenderer", "createSurface : initialized = "+initialized);
		if (!initialized) {	// Doesn't work at each attempt

	        client.getEngineZildo().initializeClient(false);
	        client.setMenuListener(new AndroidMenuListener(touchListener));
	        touchListener.init();
	        // Holes
	        client.setOpenGLGestion(Zildo.pdPlugin.openGLGestion);
	        Zildo.pdPlugin.openGLGestion.setClientEngineZildo(client.getEngineZildo());
	        
			//gl.glClearColor(0.0f, 0.0f, 0f, 0.0f);
	
	        Log.d("renderer", "init finished - start main menu");
			
	        //unused.glDisable(GL11.GL_LIGHTING);
	        
	        initialized = true;
		} else {
			// Recreate context by reloading all textures and shaders
			Log.d("openglrenderer", "recreating context");
			GLUtils.resetTexId();
			SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
			TileEngine tileEngine = ClientEngineZildo.tileEngine;
			tileEngine.loadTextures();
			Zildo.pdPlugin.initFilters(true);
			ClientEngineZildo.spriteEngine.init(spriteDisplay);
			AndroidPixelShaders.shaders.load();
		}
	}
	
	
	int i=0;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if (initialized) {
			// Clears the screen and depth buffer.
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); // | // OpenGL docs.
	                           //GLES20.GL_DEPTH_BUFFER_BIT);
	
			GLES20.glViewport(0, 0, Zildo.viewPortX, Zildo.viewPortY);
	
			i++;
			long t1=0,t2;
			
			if (i%50 == 0) {
				t1 = ZUtils.getTime();
			}
			client.mainLoop();
	
			if (i%50 == 0) {
				t2 = ZUtils.getTime();
				Log.d("time", "OpenGL ES 2 : Elapsed "+(t2-t1)+"ms");
			}
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("changed", "surface changed for "+width+"X"+height);
		if (true) return;
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL docs.
		// Select the projection matrix
		//gl.glMatrixMode(GLES20.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f,
                                   (float) width / (float) height,
                                   0.1f, 100.0f);
		// Select the modelview matrix
		//gl.glMatrixMode(GLES20.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.
	}

}