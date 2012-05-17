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
import zildo.client.stage.GameStage;
import zildo.fwk.ZUtils;
import zildo.platform.opengl.AndroidOpenGLGestion;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

public class OpenGLRenderer implements Renderer {
	
	Client client;
	TouchListener touchListener;
	GameStage game;
	
	public OpenGLRenderer(Client client, TouchListener touchListener) {
		this.client = client;
		this.touchListener = touchListener;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1.0f, 0.0f, 0f, 1.0f);
		gl.glShadeModel(GL11.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glDisable(GL11.GL_DEPTH_TEST);
		//gl.glDepthFunc(GL11.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);


		gl.glDisable(GL11.GL_CULL_FACE);

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

        

		if (Zildo.screenX != 480 || true) {	// Doesn't work at each attempt
			
			// Share GL context for other classes
	        AndroidOpenGLGestion.setGL(gl);
	        Zildo.screenX = 480;
	        Zildo.screenY = 320;
	        
	        client.getEngineZildo().initializeClient(false);
	        client.setMenuListener(new AndroidMenuListener(touchListener));
	        touchListener.init();
	        // Holes
	        client.setOpenGLGestion(Zildo.pdPlugin.openGLGestion);
	        Zildo.pdPlugin.openGLGestion.setClientEngineZildo(client.getEngineZildo());
	        
			gl.glClearColor(0.0f, 0.0f, 0f, 0.0f);
	
	        Log.d("renderer", "init finished - start main menu");
	
	
	        
	        //client.run();
	        //client.cleanUp();
			
	        gl.glDisable(GL11.GL_LIGHTING);
		}
	}
	
	int i=0;
	
	@Override
	public void onDrawFrame(GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // | // OpenGL docs.
                           //GL10.GL_DEPTH_BUFFER_BIT);

		long t1 = ZUtils.getTime();
		/*
		Zildo.pdPlugin.ortho.box(10, 10, 20, 20, 0, new Vector4f(0.5f, 0.1f, 0.3f, 1.0f));
		Zildo.pdPlugin.ortho.drawText(0,0,"Coucou zildo");
		Zildo.pdPlugin.ortho.drawText(0,100,"Coucou zildo100");
		Zildo.pdPlugin.ortho.drawText(200,200,"Coucou zildo200");
		*/
		client.mainLoop();

		long t2 = ZUtils.getTime();

		i++;
		if (i%50 == 0) {
			Log.d("time", "elapsed "+(t2-t1));
		}
		/*
		for (GameStage stage : client.getCurrentStages()) {
			stage.updateGame();
			stage.renderGame();
		}
		
		if (game != null) {
			game.updateGame();
			game.renderGame();
		} else {
			game = client.getGame();
			if (game == null) {
				client.mainLoop();
			}
		}
		*/
		
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("changed", "surface changed for "+width+"X"+height);
		if (true) return;
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL docs.
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f,
                                   (float) width / (float) height,
                                   0.1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.
	}
}