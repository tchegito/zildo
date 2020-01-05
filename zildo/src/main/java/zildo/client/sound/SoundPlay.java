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

package zildo.client.sound;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.opengl.Sound;
import zildo.fwk.opengl.SoundEngine;
import zildo.monde.WaitingSound;
import zildo.monde.util.Point;

/**
 * SoundPlay : common engine for all platforms.
 * 
 * Note: we can handle looping sounds, and recalculate their distance to the camera, to enhance player immersion
 * when he moves.
 * 
 * @author Tchegito
 *
 */
public class SoundPlay {

	private Map<AudioBank, Sound> tabSounds = new HashMap<AudioBank, Sound>();

	private List<WaitingSound> currentlyLoopingSounds = new ArrayList<WaitingSound>();
	
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
					sound.destroy();
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
			newSound.setLoop(p_sound.isLooping());
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
	public void playSoundFX(AudioBank snd, float x, float y) {
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
			if (x == 0f && y == 0f) {
				sound.play();
			} else {
/*
 * 	final static Pointf left = new Pointf(-1f, 0f);
	final static Pointf right = new Pointf(1f, 0f);
	
				float distLeft = left.distance(x, y);
				float distRight = right.distance(x, y);
				float volumeLeft = Math.max(1 - 0.1f * distLeft * distLeft, 0f);
				float volumeRight = Math.max(1 - 0.1f * distRight * distRight, 0f);
				*/
				//System.out.println("play at "+x+","+y+" ============>      "+volumeLeft+" - "+volumeRight);
				sound.playAt(x, y); // 0,0,-500);
			}
		}
	}

	public void playSoundFX(AudioBank snd) {
		playSoundFX(snd, 0f, 0f);
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

	/** Called each frame **/
	public void playSounds(List<WaitingSound> p_sounds) {
		// Every sound will be heard from the center of the screen
		// That means we don't follow hero if camera is fixed.
		Point camera = ClientEngineZildo.mapDisplay.getCamera();
		Point listeningPoint = new Point(Zildo.viewPortX >> 1, Zildo.viewPortY >> 1);	// / 2 (middle)
		/**
		 * With this code, we follow hero, and sound is calculated around him => but that doesn't suit the gameplay
		SpriteEntity p = ClientEngineZildo.spriteDisplay.getZildo();
		listeningPoint = new Point(p.getScrX(), p.getScrY());
		**/
		if (camera != null) {
			listeningPoint.x = camera.x + listeningPoint.x;
			listeningPoint.y = camera.y + listeningPoint.y;
		}
		// Set new position for every currently looping sounds
		for (WaitingSound sound : currentlyLoopingSounds) {
			float dx = 0f;
			float dy = 0f;
			if (sound.location != null) {
				dx = (sound.location.x - listeningPoint.x) / 16f / 16f;
				dy = (sound.location.y - listeningPoint.y) / 16f / 8f;
			}
			Sound snd = tabSounds.get(sound.name);
			if (snd != null) {
				snd.setPosition(dx, dy);
			}
		}
		for (WaitingSound sound : p_sounds) {
			if (sound.broadcast || sound.client == null) {
				if (!sound.isSoundFX && sound.name == null) {
					stopMusic();
				} else {
					if (!sound.isSoundFX) {
						currentMusic = (BankMusic) sound.name;
					}
					if (sound.mute) {
						stopSoundFX(sound.name);
					} else if (sound.isSoundFX || musicEnabled) {
						float dx = 0f;
						float dy = 0f;
						if (sound.location != null) {
							dx = (sound.location.x - listeningPoint.x) / 16f / 2f;
							dy = (sound.location.y - listeningPoint.y) / 16f / 8f;
						}
						if (sound.isSoundFX && ((BankSound)sound.name).isLooping()) {
							// Keep track of this looping sound if location is fixed
							currentlyLoopingSounds.add(sound);
						}
						playSoundFX(sound.name, dx, dy);
					}
				}
			}
		}
	}
	
	/**
	 * Play music with given atmosphere. For example, inside house, volume is lowered.
	 * @param p_mus
	 * @param atmo
	 */
	public void playMusic(BankMusic p_mus, Atmosphere atmo) {
		currentMusic = p_mus;
		if (musicEnabled) {
			playSoundFX(p_mus);
			// Lower music volume if inside a house
			int percentage = 100;
			if (atmo == Atmosphere.HOUSE) {
				percentage = 50;
			}
			ClientEngineZildo.soundEngine.setMusicVolume(percentage);
		}
	}

	public void disableMusic() {
		stopMusic();
		musicEnabled = false;
	}
	public void enableMusic() {
		musicEnabled = true;
		playMusic(currentMusic, null);
	}
	
	public void stopMusic() {
		BankMusic mus = ClientEngineZildo.ambient.getCurrentMusic();
		stopSoundFX(mus);
		ClientEngineZildo.ambient.setCurrentMusic(null);
	}
	
	/** Remove all looping sounds. Mandatory for map change, or game quit **/
	public void stopLooping() {
		for (WaitingSound sound : currentlyLoopingSounds) {
			stopSoundFX(sound.name);
		}
		currentlyLoopingSounds.clear();
	}
}