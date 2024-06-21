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
 
package zildo.platform.opengl;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.stage.GameStage;
import zildo.fwk.ZUtils;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.opengl.OpenGLGestion;
import zildo.platform.input.AndroidKeyboardHandler;
import zildo.server.EngineZildo;

public class AndroidOpenGLGestion extends OpenGLGestion {

	final static String title = "Zildo OpenGL";

	KeyboardHandler kbHandler = Zildo.pdPlugin.kbHandler;

	public AndroidOpenGLGestion() {
		super(title);
	}

	public AndroidOpenGLGestion(boolean fullscreen) {
		super(title, fullscreen);
		z = 0.0f;
	}

	@Override
	protected void mainloopExt() {

		EngineZildo.extraSpeed = 1;
		if (kbHandler.isKeyDown(AndroidKeyboardHandler.KEY_LSHIFT)) {
			EngineZildo.extraSpeed = 2;
		}
	}

	@Override
	public void render(boolean p_clientReady) {

		// Clear the screen and the depth buffer
		//GLES20.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); 

		//GLES20.glMatrixMode(GL11.GL_MODELVIEW);
		//GLES20.glLoadIdentity(); // Reset The model view Matrix

		// invert the y axis, down is positive
		// TODO: create an attribute to pass to the vertex buffer in order to translate
		float zz = z * 5.0f;
		if (zz != 0.0f) {
			//GLES20.glTranslatef(-zoomPosition.getX() * zz, zoomPosition.getY() * zz, 0.0f);
		}
		//GLES20.glScalef(1 + zz, -1 - zz, 1);
		if (ClientEngineZildo.filterCommand != null) {
			ClientEngineZildo.filterCommand.doPreFilter();
		}

		clientEngineZildo.renderFrame(awt);

		
		for (GameStage stage : ClientEngineZildo.getClientForGame().getCurrentStages()) {
			stage.renderGame();
		}
		
		if (ClientEngineZildo.filterCommand != null) {
			ClientEngineZildo.filterCommand.doFilter();
			ClientEngineZildo.filterCommand.doPostFilter();
		}

		if (framerate != 0) {
			//Display.sync(framerate);
		}
	}

	@Override
	protected void cleanUpExt() {
		ClientEngineZildo.cleanUp();
	}


	/**
     * Switch display with given fullscreen mode (TRUE or FALSE=windowed)
     * @param p_fullscreen
     */
	@Override
    public void switchFullscreen(boolean p_fullscreen) {
            fullscreen = p_fullscreen;
            // Always fullscreen on android !
	}

	@Override
	public void initDisplay() throws Exception {

	}

	@Override
	public void init() {
        initGL();
	}

	private void initGL() {
		// Done in OpenGLRenderer
	}

	@Override
	public void cleanUp() {
		cleanUpExt();
        //Display.destroy();
        //Mouse.destroy();
	}

	@Override
	public boolean mainloop() {
		boolean done = false;
        //if (Display.isCloseRequested()) { // Exit if window is closed
        //        done = true;
        //}
        mainloopExt();

        return done;
	}

	@Override
	public double getTimeInSeconds() {
		return ZUtils.getTime();
	}
}
