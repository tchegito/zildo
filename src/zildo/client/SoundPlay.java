package zildo.client;

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
		ZildoRecupVie("vie.wav"),
		ZildoRecupCoeur("coeur.wav"),
		ZildoRecupArgent("argent3.wav"),

		MonstreTouche("touche.wav"),
		MonstreMeurt("meurt.wav"),
		MonstreTrouve("alerte.wav"),
		
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
		
		MenuIn("Menu1.wav"),
		MenuOut("Menu2.wav"),
		MenuMove("Bip.wav"),
		MenuSelect("choix.wav"),
		MenuSelectGame("choixok.wav"),
		
		Explosion("Explose.wav");

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
	porte			X
	posebomb
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
	private OpenGLSound[] tabSounds=new OpenGLSound[Constantes.MAX_SOUNDS];
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
	
	public void finalize()
	{
		/*
		// Release all allocated buffer for samples
		for (int i=0;i<MAX_SOUNDS;i++) {
			CSound* currentSound=tabSounds[i];
			if (currentSound != null) {
				delete currentSound;
			}
		}
	
		if (soundManager) {
			delete soundManager;
		}*/
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// loadAllSoundFX
	///////////////////////////////////////////////////////////////////////////////////////
	void loadAllSoundFX() {
		for (BankSound snd : BankSound.values()) {
			// Load every sample from the sound's bank defined in SoundManagement.h
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
			chemin+="sounds\\";
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
		tabSounds[snd.ordinal()].play(); //0,0,-500);
	}

	public void playSounds(List<WaitingSound> p_sounds) {
		for (WaitingSound sound : p_sounds) {
			if (sound.broadcast || sound.client==null) {
				playSoundFX(sound.name);
			}
		}
	}
}