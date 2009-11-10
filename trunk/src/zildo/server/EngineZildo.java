package zildo.server;

import java.util.Collection;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.input.KeyboardInstant;
import zildo.monde.Game;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Point;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.state.ClientState;

public class EngineZildo {

	// Server
	public static SpriteManagement spriteManagement;
	public static MapManagement mapManagement;
	public static CollideManagement collideManagement;
	public static PlayerManagement playerManagement;
	public static PersoManagement persoManagement;
	public static DialogManagement dialogManagement;
	public static SoundManagement soundManagement;
    public static MultiplayerManagement multiplayerManagement;

    public static Game game;
    public static int compteur_animation;
	
	// For debug
	public static int extraSpeed=1;
	
	private void initializeServer(Game p_game) {
		// Inits de départ
		spriteManagement=new SpriteManagement();
		mapManagement=new MapManagement();
		persoManagement=new PersoManagement();
		dialogManagement=new DialogManagement();
		collideManagement=new CollideManagement();
		soundManagement=new SoundManagement();
		playerManagement=new PlayerManagement();
        multiplayerManagement = new MultiplayerManagement();
        
		game=p_game;

		// Charge une map
		String mapName=p_game.mapName;

		mapManagement.charge_map(mapName);
	
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
        p_zildo.setWounded(true);
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
		boolean block=false;
		for (ClientState state : p_clientStates) {
			KeyboardInstant i=state.keys;
			if (i != null) {
				// If client has pressed keys, we manage them, then clear.
				playerManagement.manageKeyboard(state);
				state.keys=null;
			}
			// Look for map change (only in single player for now)
			if (!game.multiPlayer && mapManagement.isChangingMap(state.zildo) && state.event.nature==ClientEventNature.NOEVENT) {
				ChainingPoint ch=mapManagement.getChainingPoint();
				if (ch.isBorder()) {
					state.event.nature=ClientEventNature.CHANGINGMAP_SCROLL_ASKED;
				} else {
					state.event.nature=ClientEventNature.CHANGINGMAP_ASKED;
				}
			}
			if (state.event.nature == ClientEventNature.CHANGINGMAP_SCROLL) {
				
			}
		}
		
		if (!game.multiPlayer) {
			PersoZildo zildo=persoManagement.getZildo();
			block=zildo.isInventoring();
		}
		
		// 2) Rest of the world
		collideManagement.initFrame();
		spriteManagement.updateSprites(block);
		collideManagement.manageCollisions(p_clientStates);
		mapManagement.updateMap();
		
		compteur_animation++;
	}
	
    public ClientEvent renderEvent(ClientEvent p_event) {
        ClientEvent retEvent = p_event;

        switch (p_event.nature) {
            case CHANGINGMAP_FADEOUT_OVER:
            case CHANGINGMAP_SCROLL_WAIT_MAP:
                mapManagement.processChangingMap();
                ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
                if (p_event.nature == ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP) {
                    retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL_START;
                    retEvent.angle = mapManagement.getMapScrollAngle();
                } else {
                    retEvent.nature = ClientEventNature.CHANGINGMAP_LOADED;
                }
                break;
        }
        return retEvent;
    }
	
	void loadMap(String mapname)
	{
		// Clear existing entities
		persoManagement.clearPersos();
	
		// Load map
		mapManagement.charge_map(mapname);
	}
}