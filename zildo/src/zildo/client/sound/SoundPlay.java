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

package zildo.client.sound;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;
import zildo.monde.WaitingSound;
import zildo.monde.map.Area;

/**
 * SoundPlay : common engine for all platforms.
 * 
 * Note: it seems that there is too much methods to do the same thing (playSoundFX, playMusic,
 * playMapMusic ...). Maybe a small refactor should be planned.
 * 
 * @author Tchegito
 *
 */
public class SoundPlay {

	private Map<AudioBank, Sound> tabSounds = new HashMap<AudioBank, Sound>();

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	SoundEngine soundEngine;
	// Store the current music, in case when player enable/disable music
	BankMusic currentMusic;
	boolean musicEnabled;
	
	public SoundPlay(SoundEngine soundEngine) {

		this.soundEngine = soundEngine;
		tabSounds.clear();
		loadAllSoundFX();
		musicEnabled = Zildo.soundEnabled;
	}

	public void cleanUp() {
		if (tabSounds != null) {
			// Release all allocated buffer for samples
			for (Sound sound : tabSounds.values()) {
				if (sound != null) {
					sound.finalize();
				}
				sound = null;
			}
		}
		soundEngine.cleanUp();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// loadAllSoundFX
	// /////////////////////////////////////////////////////////////////////////////////////
	void loadAllSoundFX() {
		for (BankSound snd : BankSound.values()) {
			// Load every sample from the sound's bank
			loadSound("sounds", snd);
		}
		for (BankMusic snd : BankMusic.values()) {
			// Load every music
			loadSound("musics", snd);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// loadSound
	// /////////////////////////////////////////////////////////////////////////////////////
	void loadSound(String p_subDirectory, AudioBank p_sound) {
		soundEngine.detectAndInitSoundEngine();

		if (Zildo.soundEnabled) {
			// Build entire file name
			String chemin = p_subDirectory + File.separator;
			chemin += p_sound.getFilename();
			chemin += ".";
			chemin += p_sound.getSuffix();

			Sound newSound = soundEngine.createSound(chemin);

			// Store it into the sound's tab
			tabSounds.put(p_sound, newSound);
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// playSoundFX
	// /////////////////////////////////////////////////////////////////////////////////////
	// Play sound named 'soundName' from sound's tab
	// If the given sound name isn't found, do nothing.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void playSoundFX(AudioBank snd) {
		// Play desired sound and exit
		Ambient ambient = ClientEngineZildo.ambient;
		Sound sound = tabSounds.get(snd);
		if (sound != null) {
			if (snd instanceof BankMusic) {
				if (ambient.getCurrentMusic() == snd) {
					// Current music is already the one asked : so return
					return;
				}
				ambient.setCurrentMusic((BankMusic) snd);
			}
			sound.play(); // 0,0,-500);
		}
	}

	/**
	 * Stop given sound (useful for music)
	 * 
	 * @param snd
	 */
	public void stopSoundFX(AudioBank snd) {
		Sound sound = tabSounds.get(snd);
		if (sound != null) {
			sound.stop();
		}
	}

	public void playSounds(List<WaitingSound> p_sounds) {
		for (WaitingSound sound : p_sounds) {
			if (sound.broadcast || sound.client == null) {
				if (!sound.isSoundFX && sound.name == null) {
					stopMusic();
				} else {
					if (!sound.isSoundFX) {
						currentMusic = (BankMusic) sound.name;
					}
					if (sound.isSoundFX || musicEnabled) {
						playSoundFX(sound.name);
					}
				}
			}
		}
	}

	/**
	 * Play the music related to given map.
	 * 
	 * @param p_map
	 */
	public void playMapMusic(Area p_map) {
		BankMusic mus = ClientEngineZildo.ambient.getMusicForMap(p_map);
		currentMusic = mus;
		if (musicEnabled) {
			playSoundFX(mus);
		}
	}

	public void playMusic(BankMusic p_mus) {
		currentMusic = p_mus;
		if (musicEnabled) {
			playSoundFX(p_mus);
		}
	}

	public void disableMusic() {
		stopMusic();
		musicEnabled = false;
	}
	public void enableMusic() {
		musicEnabled = true;
		playMusic(currentMusic);
	}
	
	public void stopMusic() {
		BankMusic mus = ClientEngineZildo.ambient.getCurrentMusic();
		stopSoundFX(mus);
		ClientEngineZildo.ambient.setCurrentMusic(null);
	}
}