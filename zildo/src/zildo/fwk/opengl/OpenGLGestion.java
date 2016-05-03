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

package zildo.fwk.opengl;

import java.nio.ByteBuffer;

import zildo.client.ClientEngineZildo;
import zildo.monde.util.Point;


/**
 * Class which provides all OpenGL general init behaviors :
 * <ul>
 * <li>display mode enumeration</li>
 * <li>context creation</li>
 * <li>texture load</li>
 * <li>inner loop</li>
 * </ul>
 * Needs implementation of method {@link #render()}
 * <p/>
 * 
 * Method {@link #mainloopExt()} can be overrided.
 * 
 * @author Tchegito
 * 
 */
public abstract class OpenGLGestion {

	protected final String windowTitle; // ="(To override) Window OpenGL";
	protected float lightAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // Ambient Light
																// Values ( NEW
																// )
	protected float lightDiffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f }; // Diffuse Light
																// Values ( NEW
																// )
	protected float lightPosition[] = { 0.0f, 0.0f, 2.0f, 1.0f }; // Light
																// Position (
																// NEW )

	protected float z;
	protected Point zoomPosition;
	
	protected ClientEngineZildo clientEngineZildo;
	
	protected int framerate;
	protected boolean fullscreen;
	protected boolean awt = false; // Default, no awt

	public OpenGLGestion(String p_title) {
		awt = true;
		windowTitle = p_title;
	}

	public OpenGLGestion(String p_title, boolean p_fullscreen) {
		windowTitle = p_title;
		try {
			this.fullscreen = p_fullscreen;
			initDisplay();
			init();
			switchFullscreen(fullscreen);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Switch display with given fullscreen mode (TRUE or FALSE=windowed)
	 * @param p_fullscreen
	 */
	public abstract void switchFullscreen(boolean p_fullscreen);

	public abstract void initDisplay() throws Exception;

	public abstract void init();

	
	public abstract void cleanUp();

	protected void cleanUpExt() {

	}

	public abstract boolean mainloop();

	// Defautl main loop extended : nothing. Ready to override
	protected void mainloopExt() {

	}

	public abstract void render(boolean p_clientReady);


	public void setClientEngineZildo(ClientEngineZildo p_engineZildo) {
		clientEngineZildo = p_engineZildo;
	}
	
	private double simulationTime = 0;
	private double fps;

	public void beginScene() {
		// time elapsed since we last rendered
		/*
		double secondsSinceLastFrame = getTimeInSeconds() - simulationTime;

		// update the simulation current time
		simulationTime += secondsSinceLastFrame;

		fps = 1 / secondsSinceLastFrame;
		*/
		
		// long toWait=(long) (2/(framerate*1000) -
		// secondsSinceLastFrame*1000.0f);
		/*
		 * if (toWait < 1000) { try { Thread.sleep(toWait); }catch (Exception e)
		 * {
		 * 
		 * } }
		 */
	}

	public void endScene() {

	}

	public void setZ(float p_z) {
		z = p_z;
	}

	public void setZoomPosition(Point zoomPosition) {
		this.zoomPosition = zoomPosition;
	}
	
	protected long ticksPerSecond;

	public double getFPS() {
		return fps;
	}

	public abstract double getTimeInSeconds();

	public void requestRender() {};
	
	// Default capture : nothing
	public ByteBuffer capture() { return null; };
}
