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

import zildo.fwk.opengl.Sound;
import zildo.monde.util.Pointf;
import android.media.MediaPlayer;

public class AndroidSound extends Sound {

	private final int soundId;
	private final MediaPlayer music;
	private boolean loop;
	
	private int streamId;
	
	@Override
	public void destroy() {
		
	}
	
	public AndroidSound(int soundId, MediaPlayer music) {
		this.soundId = soundId;
		this.music = music;
	}

	@Override
	public void setLoop(boolean loop) {
		this.loop = loop;
	}
	
	@Override
	public void play() {
		float volume = 1f;
		if (music != null && music != AndroidSoundEngine.currentMusic) {
			if (AndroidSoundEngine.currentMusic != null) {
				AndroidSoundEngine.currentMusic.pause();
			}
			music.setLooping(true);
			music.seekTo(0);
			music.start();
			AndroidSoundEngine.currentMusic = music;
		} else {
			int soundLoop = loop ? -1 : 0;	// -1 means loop forever
			streamId = AndroidSoundEngine.soundPool.play(soundId, volume, volume, 1, soundLoop, 1f);
		}
	}

	@Override
	public void setPosition(float x, float y) {
		float distLeft = left.distance(x, y);
		float distRight = right.distance(x, y);
		float volumeLeft = Math.max(1 - 0.1f * distLeft * distLeft, 0f);
		float volumeRight = Math.max(1 - 0.1f * distRight * distRight, 0f);
		AndroidSoundEngine.soundPool.setVolume(streamId, volumeLeft, volumeRight);
	}
	
	final static Pointf left = new Pointf(-1f, 0f);
	final static Pointf right = new Pointf(1f, 0f);
	
	@Override
	public void playAt(float x, float y) {
		if (music != null) {
			play();
		} else {
			float distLeft = left.distance(x, y);
			float distRight = right.distance(x, y);
			float volumeLeft = Math.max(1 - 0.1f * distLeft * distLeft, 0f);
			float volumeRight = Math.max(1 - 0.1f * distRight * distRight, 0f);

			int soundLoop = loop ? -1 : 0;	// -1 means loop forever
			streamId = AndroidSoundEngine.soundPool.play(soundId, volumeLeft, volumeRight, 1, soundLoop, 1f);
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