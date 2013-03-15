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

import zildo.Zildo;
import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

/**
 * @author Tchegito
 *
 */
public class AndroidSoundEngine extends SoundEngine {

	static SoundPool soundPool;
	float streamVolumeCurrent;
	float streamVolumeMax;
	
	static MediaPlayer currentMusic;
	
	@Override
	public Sound createSound(String path) {
		boolean music = path.contains("music");
		AssetFileDescriptor afd = (AssetFileDescriptor) Zildo.pdPlugin.openFd(path);
		MediaPlayer mp = null;
		int soundId = 0;
		
		if (music) {
			try {
				mp = new MediaPlayer();
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				afd.close();//Don't forget to close, the documentation of setDataSource is very specific about it
	
				mp.prepare();
			} catch (Exception e) {
				Log.e("sound", "can't create music "+path);
			}
		} else {
			soundId = soundPool.load(afd, 1);
		}

		
		return new AndroidSound(soundId, mp);
	}
	
	@Override
	public void detectAndInitSoundEngine() {
		if (!initialized) {
			soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
			currentMusic = null;
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
	
	public static void pauseAll() {
		if (currentMusic != null) {
			currentMusic.pause();
		}
		if (soundPool != null) {
			soundPool.autoPause();
		}
	}
	
	public static void resumeAll() {
		if (currentMusic != null) {
			currentMusic.start();
		}
		if (soundPool != null) {
			soundPool.autoResume();
		}
	}
}
