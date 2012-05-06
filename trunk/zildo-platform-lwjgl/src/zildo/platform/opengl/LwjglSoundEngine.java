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

package zildo.platform.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.newdawn.slick.openal.SoundStore;

import zildo.Zildo;
import zildo.fwk.ZUtils;
import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;

/**
 * @author Tchegito
 *
 */
public class LwjglSoundEngine extends SoundEngine {

	@Override
	public Sound createSound(String path) {
		return new LwjglSound(path);
	}
	
	@Override
	public void detectAndInitSoundEngine() {
		if (failed || initialized || !Zildo.soundEnabled) {
			return;	// No need to try once more
		}
		try {
			//AL.create();
			//ZUtils.sleep(1000);
			//AL.destroy();
			//ZUtils.sleep(1000);
			SoundStore.get().init();
			initialized = true;
		} catch (Exception e) {
			Zildo.soundEnabled = false;
			failed = true;	// Avoid to reinit another time
		}
	}
	
	/**
	 * Check the current error state of AL
	 * @return true if there was an error. false if 'AL_NO_ERROR' (no error).
	 */
	private boolean checkOpenALErrors() {
		boolean ret = true;

		switch (AL10.alGetError()) {
		case AL10.AL_NO_ERROR:
			ret = false;
			break;
		case AL10.AL_INVALID_NAME:
			System.out.println("Invalid name parameter.");
			break;
		case AL10.AL_INVALID_ENUM:
			System.out.println("Invalid parameter.");
			break;
		case AL10.AL_INVALID_VALUE:
			System.out.println("Invalid enum parameter value.");
			break;
		case AL10.AL_INVALID_OPERATION:
			System.out.println("Illegal call.");
			break;
		case AL10.AL_OUT_OF_MEMORY:
			System.out.println("Unable to allocate memory.");
		}
		
		if (ret) {
			System.out.println("- Error in AL.");
		}

		return ret;
	}
	
	/**
	 * Check the current error state of ALC
	 * @return true if there was an error. false if 'ALC_NO_ERROR' (no error).
	 */
	private boolean checkOpenALCErrors() {
		boolean ret = true;

		switch (AL10.alGetError()) {
		case ALC10.ALC_NO_ERROR:
			ret = false;
			break;
		case ALC10.ALC_INVALID_DEVICE:
			System.out.println("The device handle or specifiers names an inaccessible driver/server");
			break;
		case ALC10.ALC_INVALID_CONTEXT:
			System.out.println("The Context argument does not name a valid context.");
			break;
		case ALC10.ALC_INVALID_ENUM:
			System.out.println("A token used is not valid, or not applicable.");
			break;
		case ALC10.ALC_INVALID_VALUE:
			System.out.println("A value (e.g. Attribute is not valid, or not applicable.");
			break;
		case ALC10.ALC_OUT_OF_MEMORY:
			System.out.println("Unable to allocate memory.");
			break;
		}
		
		if (ret) {
			System.out.println("- Error in ALC.");
		}

		return ret;
	}
	
	private boolean checkErrors() {
		return this.checkOpenALErrors() || this.checkOpenALCErrors();
	}

	@Override
	public void pollMusic(int delta) {
		try {
			SoundStore.get().poll(delta);
		} catch (Throwable t) {
			// Not a big deal : music is wrapping
			System.out.println("coucou");
		}
	}
	

	@Override
	public void cleanUp() {
		AL.destroy();
	}
}
