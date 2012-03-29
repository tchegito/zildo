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

import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * @author Tchegito
 *
 */
public class AndroidSoundEngine extends SoundEngine {

	SoundPool soundPool;
	
	@Override
	public Sound createSound(String path) {
		int soundId = 1; //soundPool.load("resources/musics/Angoisse.ogg", 1);
		return new AndroidSound(soundId);
	}
	
	@Override
	public void detectAndInitSoundEngine() {
		if (!initialized) {
			//soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			//int soundId = soundPool.load("resources/musics/Angoisse.ogg", 1);
			//soundPool.play(soundId, 1, 1, 1, 0, 1);
			initialized = true;
		}
	}
	

	@Override
	public void pollMusic(int delta) {
	}
	

	@Override
	public void cleanUp() {
		if (soundPool != null) {
			soundPool.release();
			soundPool = null;	// Is that really helpful ?
		}
		initialized=false;
	}
}
