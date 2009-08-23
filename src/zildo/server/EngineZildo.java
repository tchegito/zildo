package zildo.server;

import java.util.Collection;

import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Game;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.Point;
import zildo.monde.persos.PersoZildo;

public class EngineZildo {

	// Server
	public static SpriteManagement spriteManagement;
	public static MapManagement mapManagement;
	public static CollideManagement collideManagement;
	public static PlayerManagement playerManagement;
	public static PersoManagement persoManagement;
	public static DialogManagement dialogManagement;
	public static SoundManagement soundManagement;
    public static MessageManagement messageManagement;

    public static Game game;
    public static int compteur_animation;
    
	private static int timeToWait=0;
	private static int nFramesToWait=0;
	
	// For debug
	public static int extraSpeed=1;
	
	public static void freeze() {
		timeToWait=3000;
		nFramesToWait=3;
	}
	
	public void waitIfFreezed() {
		if (nFramesToWait>0) {
			try {
				Thread.sleep(timeToWait);
			} catch (Exception e) {
				
			}
			nFramesToWait--;
		}
	}
	
	private void initializeServer(Game p_game) {
		// Inits de départ
		spriteManagement=new SpriteManagement();
		mapManagement=new MapManagement();
		persoManagement=new PersoManagement();
		dialogManagement=new DialogManagement();
		collideManagement=new CollideManagement();
		soundManagement=new SoundManagement();
		playerManagement=new PlayerManagement();
        messageManagement = new MessageManagement();
        
		// Charge une map
		String mapName=p_game.mapName;

		mapManagement.charge_map(mapName);
	
		game=p_game;
	}

    public int spawnClient() {

        Point respawnLocation = mapManagement.getRespawnPosition();
        PersoZildo zildo = new PersoZildo(respawnLocation.getX(), respawnLocation.getY());
        spriteManagement.spawnPerso(zildo);

        return zildo.getId();
    }

    /**
     * Zildo comes to death, so respawn him another place.
     * @param p_zildo
     */
    static public void respawnClient(PersoZildo p_zildo) {
        Point respawnLocation = mapManagement.getRespawnPosition();
        p_zildo.placeAt(respawnLocation);
        p_zildo.setPv(13);
        p_zildo.beingWounded(0, 0, null);
    }

	public EngineZildo(Game p_game) {
		initializeServer(p_game);
	}
	
	public void cleanUp() {

	}
	
	public void finalize()
	{
		// L'ordre des suppression est TRES important ! En effet, le vidage des cartes passe
		// par le vidage de tous les sprites/persos qui y sont référencés. Donc on a besoin
		// d'avoir à ce moment là l'objet 'spriteManagement'.
		/*
		delete mapManagement;
		delete spriteManagement;
		delete persoManagement;
		delete playerManagement;
		delete guiManagement;
		delete dialogManagement;
		delete soundManagement;
		delete collideManagement;
	*/
	}	
	
	public void renderFrame(Collection<ClientState> p_clientStates) {
		// Animate the world
		// 1) Players
		for (ClientState state : p_clientStates) {
			KeyboardInstant i=state.keys;
			if (i != null) {
				// If client has pressed keys, we manage them, then clear.
				playerManagement.manageKeyboard(state);
				state.keys=null;
			}
		}
		// 2) Rest of the world
		collideManagement.initFrame();
		spriteManagement.updateSprites();
		collideManagement.manageCollisions(p_clientStates);
		mapManagement.updateMap();
		
		compteur_animation++;
	}
	
	void loadMap(String mapname)
	{
		// Clear existing entities
		persoManagement.clearPersos();
	
		// Load map
		mapManagement.charge_map(mapname);
	}
}