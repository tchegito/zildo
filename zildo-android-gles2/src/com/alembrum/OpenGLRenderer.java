/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

package com.alembrum;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.client.gui.menu.SaveGameMenu;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.fwk.net.www.CrashReporter;
import zildo.platform.opengl.AndroidPixelShaders;
import zildo.platform.opengl.utils.GLUtils;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class OpenGLRenderer implements Renderer {
	
	Client client;
	TouchListener touchListener;
	Handler handler;
	
	boolean initialized = false;
	
	public OpenGLRenderer(Client client, TouchListener touchListener, Handler handler) {
		this.client = client;
		this.touchListener = touchListener;
		this.handler = handler;

		// When this class is recreated and there was a previous context, we need to reinitialize the ID
		//GLUtils.resetTexId();
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0f, 1.0f);
		GLES20.glClearDepthf(1.0f);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glDisable(GLES20.GL_CULL_FACE);


		Log.d("openglrenderer", "createSurface : initialized = "+initialized);
		if (!initialized) {	// Doesn't work at each attempt

	        client.getEngineZildo().initializeClient(false);
	        client.setMenuListener(new AndroidMenuListener(touchListener));
	        touchListener.init();
	        // Holes
	        client.setOpenGLGestion(Zildo.pdPlugin.openGLGestion);
	        Zildo.pdPlugin.openGLGestion.setClientEngineZildo(client.getEngineZildo());
	
	        Log.d("renderer", "init finished - start main menu");
	        
	        initialized = true;
		} else {
			// Recreate context by reloading all textures and shaders
			Log.d("openglrenderer", "recreating context");
			// Issue 139: recalculate ratio if screen was different last time
    		touchListener.calculateRatios();

			GLUtils.resetTexId();
			SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
			TileEngine tileEngine = ClientEngineZildo.tileEngine;
			tileEngine.loadTextures();
			ClientEngineZildo.filterCommand.recreateContext();
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
			/*
			i++;
			long t1=0,t2;
			
			if (i%50 == 0) {
				t1 = ZUtils.getTime();
			}
			*/
			try {
				client.mainLoop();
			} catch (RuntimeException e) {
				// Send a more detailed report than Google one
				new CrashReporter(e).addContext().sendReport();
				// Try to save on crash
				int slot = SaveGameMenu.saveOnCrash();
				if (slot != -1) {
		    		Message message = new Message();
		    		message.what = ZildoActivity.TOAST;
		    		message.arg1 = slot;
		    		handler.sendMessage(message);
				}
				// End this game and return to main menu
				client.quitGame();
				client.handleMenu(new StartMenu());
				
				// Now we don't throw exception anymore

			}
	
			/*
			if (i%50 == 0) {
				t2 = ZUtils.getTime();
				Log.d("time", "OpenGL ES 2 : Elapsed "+(t2-t1)+"ms");
			}*/
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d("changed", "surface changed for "+width+"X"+height);
	}

}