/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import zildo.fwk.opengl.Sound;
import android.media.MediaPlayer;

public class AndroidSound extends Sound {

	private final int soundId;
	private final MediaPlayer music;
	
	private int streamId;
	
	@Override
	public void finalize() {
		
	}
	
	public AndroidSound(int soundId, MediaPlayer music) {
		this.soundId = soundId;
		this.music = music;
	}
	

	@Override
	public void play() {
		float volume = 1f;
		if (music != null) {
			if (AndroidSoundEngine.currentMusic != null) {
				AndroidSoundEngine.currentMusic.pause();
			}
			music.setLooping(true);
			music.seekTo(0);
			music.start();
			AndroidSoundEngine.currentMusic = music;
		} else {
			int loop = 0;	// No loop
			streamId = AndroidSoundEngine.soundPool.play(soundId, volume, volume, 1, loop, 1f);
		}
	}

	@Override
	public void stop() {
		if (music != null) {
			music.pause();
			AndroidSoundEngine.currentMusic = null;
		} else if (streamId != 0) {
			AndroidSoundEngine.soundPool.stop(streamId);
		}
	}

	public void pause() {
		if (music != null) {
			music.pause();
		} else if (streamId != 0) {
			AndroidSoundEngine.soundPool.pause(streamId);
		}
	}
}