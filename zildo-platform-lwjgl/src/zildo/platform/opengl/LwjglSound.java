/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;

import zildo.fwk.opengl.Sound;
import zildo.resource.Constantes;

public class LwjglSound extends Sound {

	Audio snd = null;

	boolean music;
	
	@Override
	public void finalize() {
		AL.destroy();
	}
	
	public LwjglSound(String p_filename) {
		loadALData(p_filename);
	}

	private int loadALData(String p_filename) {
		String format = p_filename.substring(p_filename.length() - 3).toUpperCase();
		File file=new File(Constantes.DATA_PATH + p_filename);
		
		try {
			System.out.println(p_filename);
			music = "OGG".equals(format);
			if (music) {
				snd = SoundStore.get().getOggStream(Constantes.DATA_PATH+p_filename);
			} else {
				InputStream stream=new FileInputStream(file);
				snd = AudioLoader.getAudio(format, new BufferedInputStream(stream));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// Do another error check and return.
		if (AL10.alGetError() == AL10.AL_NO_ERROR)
			return AL10.AL_TRUE;

		return AL10.AL_FALSE;

	}

	@Override
	public void play() {
		if (music) {
			snd.playAsMusic(1.0f, 1.0f, true);
		} else {
			snd.playAsSoundEffect(1.0f, 1.0f, false);
		}
	}

	@Override
	public void playAt(float x, float y) {
		if (music) {
			play();
		} else {
			snd.playAsSoundEffect(1.0f, 1.0f, false, x, y, 0);
		}
	}
	
	@Override
	public void stop() {
		snd.stop();
	}

	public void pause() {
		snd.stop();
	}
}