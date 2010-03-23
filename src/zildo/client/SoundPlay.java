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

package zildo.client;

import java.io.File;
import java.util.List;

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


	public enum BankSound {
		ZildoAttaque("epee.wav"),
		
		ZildoTouche("zildoaie.wav"),
		ZildoTombe("tombe.wav"),
		ZildoAtterit("atterit.wav"),
		ZildoLance("lance.wav"),
		ZildoRamasse("ramasse.wav"),
		ZildoPlonge("eau.wav"),
		ZildoNage("eau2.wav"),
		ZildoPatauge("eau3.wav"),
		ZildoGadou("vase.wav"),
		ZildoRecupVie("vie.wav"),
		ZildoRecupCoeur("coeur.wav"),
		ZildoRecupArgent("argent3.wav"),
		ZildoGagneArgent("argent.wav"),
		ZildoOuvreCoffre("coffre.wav"),
		ZildoCogne("choc.wav"),
		ZildoPousse("pousse.wav"),
		ZildoMonte("escalmon.wav"),
		ZildoDescend("escaldes.wav"),
		ZildoTrouve("trouve.wav"),
		
		QuadDamage("quaddamage.wav"),
		QuadDamageLeaving("damage2.wav"),
		QuadDamaging("damage3.wav"),
		
		MonstreTouche("touche.wav"),
		MonstreTouche2("touche2.wav"),
		MonstreMeurt("meurt.wav"),
		MonstreTrouve("alerte.wav"),
		MonstreTire("tire2.wav"),
		
		Poule1("poule1.wav"),
		Poule2("poule2.wav"),
		
		CasseBuisson("feuilles.wav"),
		CassePierre("brise.wav"),
		OuvrePorte("porte.wav"),

		AfficheTexte("texte1.wav"),
		AfficheTexte2("texte2.wav"),
		AfficheTexteFin("textefin.wav"),

		FlecheTir("tire.wav"),
		FlechePlante("plante.wav"),
		
		Boomerang("boomeran.wav"),
		BoomerangTape("argent2.wav"),
		
		MenuIn("menu1.wav"),
		MenuOut("menu2.wav"),
		MenuMove("bip.wav"),
		MenuSelect("choix.wav"),
		MenuSelectGame("choixok.wav"),
		MenuOutOfOrder("out.wav"),
		
		PlanteBombe("posebomb.wav"),
		Explosion("explose.wav");

		String filename;
		
		private BankSound(String p_filename) {
			filename=p_filename;
		}
	 };

	/*				Description		Utilisation
	alarme		
	alerte
	argent
	argent2							
	argent3			X				X
	atterit			X
	brise			X
	boomeran		X				X
	carte
	choix
	choixok
	coeur			X				X
	eau2			X
	eau				X
	epee			X				X
	explose			X				X
	feuilles		X				X
	flamme
	herbe
	lance			X
	meurt			X				X
	pasvite
	porte			X				X
	posebomb		X				X
	ramasse			X
	texte1			X				X
	texte2			X
	textefin		X				X
	tombe			X
	touche			X				X
	vie				X				X
	zildoaie		X				X
	*/
	
	//CSoundManager* soundManager;
	private OpenGLSound[] tabSounds=new OpenGLSound[BankSound.values().length];
	private int nSounds;
	//const GUID GUID_null = { 0, 0, 0, { 0, 0, 0, 0, 0, 0, 0, 0 } };
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public SoundPlay()
	{

		// Load every samples
		nSounds=0;
		for (int i=0;i<Constantes.MAX_SOUNDS;i++) {
			tabSounds[i]=null;
		}
		loadAllSoundFX();
	}
	
	public void cleanUp()
	{
		
		// Release all allocated buffer for samples
		for (int i=0;i<Constantes.MAX_SOUNDS;i++) {
			OpenGLSound sound=tabSounds[i];
			if (sound != null) {
				sound.finalize();
			}
			sound=null;
		}
		OpenGLSound.cleanUp();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadAllSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
	void loadAllSoundFX() {
		for (BankSound snd : BankSound.values()) {
			// Load every sample from the sound's bank
			loadSoundFX(snd.filename);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
	void loadSoundFX(String soundName) {
		if (Zildo.soundEnabled) {
			// Build entire file name
			String chemin=Constantes.DATA_PATH;
			chemin+="sounds"+File.separator;
			chemin+=soundName;
	
			OpenGLSound newSound=new OpenGLSound(chemin);
	
			// Store it into the sound's tab
			tabSounds[nSounds]=newSound;
		
			nSounds++;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// playSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
	// Play sound named 'soundName' from sound's tab
	// If the given sound name isn't found, do nothing.
	///////////////////////////////////////////////////////////////////////////////////////
	public void playSoundFX(BankSound snd) {
		// Play desired sound and exit
		OpenGLSound sound=tabSounds[snd.ordinal()];
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