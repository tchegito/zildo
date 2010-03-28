/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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
import zildo.fwk.opengl.OpenGLSound;
import zildo.monde.WaitingSound;
import zildo.prefs.Constantes;

// SoundManagement.cpp: implementation of the SoundManagement class.
//
// V1.00: -load a bank of sound, defined in SoundManagement.h
//        -play sample on demand,without additional parameters.
//////////////////////////////////////////////////////////////////////

public class SoundPlay {


	//CSoundManager* soundManager;
	private Map<AudioBank, OpenGLSound> tabSounds=new HashMap<AudioBank, OpenGLSound>();
	private int nSounds;
	//const GUID GUID_null = { 0, 0, 0, { 0, 0, 0, 0, 0, 0, 0, 0 } };
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SoundPlay()
	{

		// Load every samples
		nSounds=0;
		tabSounds.clear();
		loadAllSoundFX();
	}
	
	public void cleanUp()
	{
		if (tabSounds != null) {
			// Release all allocated buffer for samples
			for (OpenGLSound sound : tabSounds.values()) {
				if (sound != null) {
					sound.finalize();
				}
				sound=null;
			}
		}
		OpenGLSound.cleanUp();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadAllSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
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
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadSound
	///////////////////////////////////////////////////////////////////////////////////////
	void loadSound(String p_subDirectory, AudioBank p_sound) {
		if (Zildo.soundEnabled) {
			// Build entire file name
			String chemin=Constantes.DATA_PATH;
			chemin+=p_subDirectory+File.separator;
			chemin+=p_sound.getFilename();
			chemin+=".";
			chemin+=p_sound.getSuffix();
	
			OpenGLSound newSound=new OpenGLSound(chemin);
	
			// Store it into the sound's tab
			tabSounds.put(p_sound, newSound);
		
			nSounds++;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// playSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
	// Play sound named 'soundName' from sound's tab
	// If the given sound name isn't found, do nothing.
	///////////////////////////////////////////////////////////////////////////////////////
	public void playSoundFX(AudioBank snd) {
		// Play desired sound and exit
		OpenGLSound sound=tabSounds.get(snd);
		if (sound != null) {
			sound.play(); //0,0,-500);
		}
	}

	public void playSounds(List<WaitingSound> p_sounds) {
		for (WaitingSound sound : p_sounds) {
			if (sound.broadcast || sound.client==null) {
				playSoundFX(sound.name);
			}
		}
	}
}