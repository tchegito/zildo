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



/**
 * @author Tchegito
 *
 */
public abstract class SoundEngine {


	protected boolean initialized = false;
	protected boolean failed = false;
	protected int musicVolume = 100;
	private int percentageBefore;
	
	protected int counter = 0;
	
	public abstract void detectAndInitSoundEngine();
	protected abstract void pollMusic(int delta);
	public abstract void setMusicVolume(int percentage);

	public void poll(int delta) {
		pollMusic(delta);
		if (counter -- == 1) {
			setMusicVolume(percentageBefore);
			counter = 0;
		}
	}

	public abstract void cleanUp();
	
	/** Load sound file, either music or SFX **/
	public abstract Sound createSound(String path);
	
	
	public void lowerTemporarilyMusicVolume() {
		percentageBefore = musicVolume;
		setMusicVolume(musicVolume /2);
		counter = 200;
	}
}
