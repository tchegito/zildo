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

public class AndroidSound extends Sound {

	private final int soundId;
	
	@Override
	public void finalize() {
		
	}
	
	public AndroidSound(int soundId) {
		this.soundId = soundId;
	}
	

	@Override
	public void play() {
		/*
		if (snd != null) {
			snd.playAsMusic(1.0f, 1.0f, true);
		} else {
			AL10.alSourcePlay(source.get(0));
		}*/
	}

	@Override
	public void stop() {
		/*
		if (snd != null) {
			snd.stop();
		} else {
			AL10.alSourceStop(source.get(0));
		}*/
	}

	public void pause() {
		/*
		if (snd != null) {
			snd.stop();
		} else {
			AL10.alSourcePause(source.get(0));
		}*/
	}
}