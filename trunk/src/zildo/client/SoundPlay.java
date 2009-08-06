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



	static String bankSound[][]={   {"ZildoAttaque",    "epee.wav"},
									{"ZildoTouche",     "zildoaie.wav"},
									{"ZildoTombe",      "tombe.wav"},
									{"ZildoAtterit",    "atterit.wav"},
									{"ZildoLance",      "lance.wav"},
									{"ZildoRamasse",    "ramasse.wav"},
									{"ZildoPlonge",     "eau.wav"},
									{"ZildoNage",       "eau2.wav"},
									{"ZildoRecupVie",   "vie.wav"},
									{"ZildoRecupCoeur", "coeur.wav"},
									{"ZildoRecupArgent","argent3.wav"},

									{"MonstreTouche",	"touche.wav"},
									{"MonstreMeurt",	"meurt.wav"},
									{"MonstreTrouve",	"alerte.wav"},
									
									{"CasseBuisson",    "feuilles.wav"},
									{"CassePierre",     "brise.wav"},
									{"OuvrePorte",      "porte.wav"},

									{"AfficheTexte",    "texte1.wav"},
									{"AfficheTexte2",   "texte2.wav"},
									{"AfficheTexteFin", "textefin.wav"}

								 };

	/*				Description		Utilisation
	alarme		
	alerte
	argent
	argent2							
	argent3			X				X
	atterit			X
	brise			X
	carte
	choix
	choixok
	coeur			X				X
	eau2			X
	eau				X
	epee			X				X
	explose
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
	vie				X
	zildoaie		X
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
		for (int i=0;i<(bankSound.length);i++) {
			// Load every sample from the sound's bank defined in SoundManagement.h
			String[] sound=bankSound[i];
			loadSoundFX(sound[1]);
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
	public void playSoundFX(String soundName) {
		for (int i=0;i<nSounds;i++) {
			String[] sound=bankSound[i];
			String name=sound[0];
			if (soundName.equals(name)) {
				// Play desired sound and exit
				tabSounds[i].play(); //0,0,-500);
				return;
			}
		}
	}
	
	public void playSounds(List<WaitingSound> p_sounds) {
		for (WaitingSound sound : p_sounds) {
			playSoundFX(sound.name);
		}
	}
}