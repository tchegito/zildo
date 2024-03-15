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

package zildo.server;

import java.util.Collection;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.input.MovementRecord;
import zildo.fwk.net.www.WorldRegister;
import zildo.monde.Game;
import zildo.monde.Hasard;
import zildo.monde.dialog.DialogManagement;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.ChainingPoint.MapLink;
import zildo.monde.sprites.desc.ZildoOutfit;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.resource.KeysConfiguration;
import zildo.server.state.ClientState;
import zildo.server.state.ScriptManagement;

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
    public static ScriptManagement scriptManagement;
    public static WorldRegister worldRegister;
    
    public static Game game;
    public static Hasard hasard = new Hasard();	// Could be overwrited for UT
    private static EasyBuffering backedUpGame;	// When hero dies, we restore this game
    public static int nFrame;
	
    public static ClientState spClientState;	// Single player client state
    
    private static ClientEvent askedEvent;
    
	// For debug
	public static int extraSpeed=1;
	
	private void initializeServer(Game p_game) {
        setGame(p_game);

        // Inits de départ
		spriteManagement=new SpriteManagement();
		mapManagement=new MapManagement();
		persoManagement=new PersoManagement();
		dialogManagement=new DialogManagement();
		collideManagement=new CollideManagement();
		soundManagement=new SoundManagement();
		playerManagement=new PlayerManagement();
        multiplayerManagement = new MultiplayerManagement();
        synchronized (this) {	// See Issue 102
	        scriptManagement = null;	// Mandatory, because next script read could use previous context values
	        scriptManagement = new ScriptManagement();  
        }
		// Charge une map
		String mapName=p_game.mapName;
		if (mapName != null) {
			mapManagement.loadMap(mapName, false);
		}
		
		nFrame = 0;
		
		worldRegister = new WorldRegister();
	}

	static public int spawnClient(ZildoOutfit p_outfit) {

        Point respawnLocation = mapManagement.getRespawnPosition();
        PersoPlayer zildo = new PersoPlayer(respawnLocation.getX(), respawnLocation.getY(), p_outfit);
        spriteManagement.spawnPerso(zildo);

        if (game.multiPlayer) {
        	zildo.resetForMultiplayer();
        }
        return zildo.getId();
    }

    /**
     * Zildo comes to death, so respawn him another place.
     * @param p_zildo
     */
    static public void respawnClient(PersoPlayer p_zildo) {
        Point respawnLocation = mapManagement.getRespawnPosition();
        
        p_zildo.resetForMultiplayer();
        
        p_zildo.placeAt(respawnLocation);
    }

	public EngineZildo(Game p_game) {
		initializeServer(p_game);
	}
	
	public void cleanUp() {

	}
	
	//TODO: confuse between state.zildo and persoManagement.getZildo ==> one should be removed
	//TODO: client.isIngameMenu should be replaced by an attribute in ClientState
	public void renderFrame(Collection<ClientState> p_clientStates) {
		
		// Demo replay
		if (Zildo.replayMovements) {
			ClientState state = p_clientStates.iterator().next();
			for (KeysConfiguration k : KeysConfiguration.values()) {
				state.keys.setKey(k, false);
			}
			for (MovementRecord rec : game.getMovements()) {
				if (rec.frame > nFrame) break;
				if (rec.frame == nFrame) {
					KeysConfiguration key = KeysConfiguration.values()[rec.key];
					state.keys.setKey(key, true);
				}
			}
		}
		// Animate the world
		// 1) Players
		boolean block=false;
		boolean blockKeyboard=false;
		boolean blockCollision = false;
		PersoPlayer zildo = persoManagement.getZildo();
		for (ClientState 
				state : p_clientStates) {
			
			blockKeyboard = ClientEngineZildo.client.isIngameMenu();
			blockKeyboard |=ClientEngineZildo.guiDisplay.getToDisplay_dialogMode() == DialogMode.TEXTER;
			if (game.multiPlayer) {
				multiplayerManagement.render();
			} else {	// Block everything in single player
				block = blockKeyboard;
				block |= state.event.nature == ClientEventNature.CHANGINGMAP_SCROLL; 
				block |= (zildo != null && zildo.isInventoring());

				// We'll block collision checking during map fade (see Issue 127)
				blockCollision = block;
				blockCollision |= state.event.nature == ClientEventNature.FADING_OUT; 
				blockCollision |= state.event.nature == ClientEventNature.FADING_IN; 

			}
			
			// If client has pressed keys and he's not blocked, we manage them, then clear.
			if (state.keys != null && !blockKeyboard) {
				playerManagement.manageKeyboard(state);
			}
			state.keys=null;
			
			// Look for map change (only in single player for now)
			if (askedEvent == null && !game.multiPlayer && zildo != null && zildo.isAlive() && !state.event.mapChange 
					&& /*state.event.nature==ClientEventNature.NOEVENT*/ mapManagement.isChangingMap(state.zildo) ) {
				ChainingPoint ch=mapManagement.getChainingPoint();
				if (ch.isBorder()) {
					state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_SCROLL_ASKED);
					state.event.chPoint=ch;
				} else if (!state.event.script) {
					MapLink linkType=ch.getLinkType();
					if (ch.isDone() || linkType == MapLink.REGULAR) {
						state.event=new ClientEvent(ClientEventNature.CHANGINGMAP_ASKED, ch.getTransitionAnim());
					} else {
						String scriptName = linkType.scriptIn;
						if (linkType == MapLink.PIT) {
							// Center hero on the chaining point
							Point middle = ch.getZone(null).getCenter();
							zildo.setX(middle.x+2);
							zildo.setY(middle.y+2);
						}
						EngineZildo.scriptManagement.execute(scriptName, true);

						state.event.nature=ClientEventNature.SCRIPT;
						ch.setDone(true);
					}
					state.event.chPoint=ch;
				}
				state.event.mapChange=state.event.chPoint != null; //true;
			} else if (askedEvent != null) { // && !state.event.mapChange) {
				// Use the asked event and reset it (only if we aren't changing map)
				state.event.nature=askedEvent.nature;
				state.event.effect=askedEvent.effect;
				if (askedEvent.chPoint != null) {
					//TODO: check this ! It's sensitive
					state.event.chPoint=askedEvent.chPoint;
				}
				//TODO: try to see if this is really okay
				//if (askedEvent.mapChange) { 
					state.event.mapChange = askedEvent.mapChange;
				//}
				askedEvent=null;
			}
		}

		
		collideManagement.initFrame();

		if (!block) {
			scriptManagement.render();
		}
		
		// 2) Rest of the world
		spriteManagement.updateSprites(block);
		if (!blockCollision) {
			collideManagement.manageCollisions(p_clientStates);
		}
		mapManagement.updateMap();
		
		nFrame++;
	}
	
    public ClientEvent renderEvent(ClientEvent p_event) {
        ClientEvent retEvent = p_event;

        switch (p_event.nature) {
            case FADEOUT_OVER:
            case CHANGINGMAP_SCROLL_WAIT_MAP:
            	if (p_event.chPoint != null) {
	                mapManagement.processChangingMap(p_event.chPoint);
	                ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
	                ClientEngineZildo.mapDisplay.setPreviousMap(EngineZildo.mapManagement.getPreviousMap());
	                if (p_event.nature == ClientEventNature.CHANGINGMAP_SCROLL_WAIT_MAP) {
	                    retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL_START;
	                    retEvent.angle = mapManagement.getMapScrollAngle();
	                } else {
	                	// Do the right animation when Zildo came on the new map
	                	ChainingPoint ch = mapManagement.getChainingPoint();
	                	if (ch.getComingAngle() == Angle.NULL) {
		                	// Zildo is just falling
		                	EngineZildo.scriptManagement.execute("endFallPit", true);
							retEvent.nature=ClientEventNature.SCRIPT;
							retEvent.mapChange=false;
	                	} else {
							MapLink linkType=MapLink.REGULAR;
							if (ch != null) {
								linkType=ch.getLinkType();
							}
							switch (linkType) {
								default:
									String scriptName = linkType.scriptOut;
									EngineZildo.scriptManagement.execute("backupGame", true);
									EngineZildo.scriptManagement.execute(scriptName, true);
									retEvent.nature = ClientEventNature.SCRIPT;
									break;
								case REGULAR:
									retEvent.nature = ClientEventNature.CHANGINGMAP_LOADED;
									break;
								case PIT:
									break;
							}
	                	}
						retEvent.chPoint = null;
	                }
            	} else {
            		retEvent.nature = ClientEventNature.NOEVENT;
            	}
                break;
			case CHANGINGMAP_WAITSCRIPT:	// Engine is doing 'map script' (see MapscriptElement)
				if (!scriptManagement.isPriorityScripting()) {
					retEvent.nature = ClientEventNature.CHANGINGMAP_SCROLL;
					// We have to wait condition mapscript to execute, in case they change the music (example: igorlily/igorvillage)
					EngineZildo.mapManagement.loadMapMusic();
				}
				retEvent.chPoint = null;
				break;
            case CHANGINGMAP_SCROLLOVER:
            	persoManagement.getZildo().setGhost(false);
            	mapManagement.notifiyScrollOver();
            	spriteManagement.clearSuspendedEntities();
                ClientEngineZildo.mapDisplay.setPreviousMap(EngineZildo.mapManagement.getPreviousMap());
            	retEvent.nature = ClientEventNature.NOEVENT;
            	retEvent.mapChange = false;
            	retEvent.chPoint = null;
            	if (EngineZildo.scriptManagement.isAllowedToSave()) {
            		EngineZildo.backUpGame();
            	}
            	break;
            case DIALOG_FULLDISPLAY:
	        	dialogManagement.setFullSentenceDisplayed();
	        	retEvent.nature = ClientEventNature.NOEVENT;
	        default:
	        	break;
        }

        retEvent.script=scriptManagement.isBlockedScripting();
        
        return retEvent;
    }

    public static void setBackedUpGame(EasyBuffering p_buffer) {
    	backedUpGame = p_buffer;
    }
	
    public static void backUpGame() {
		EasyBuffering buffer = new EasyBuffering();
		game.serialize(buffer);
		buffer.getAll().flip();
    }
    
    public static void restoreBackedUpGame() {
    	backedUpGame.getAll().position(0);
    	SaveGameMenu.loadGameFromBuffer(backedUpGame, false);
    }
    
	public static void askEvent(ClientEvent p_event) {
		askedEvent=p_event;
	}

	// This is the client state for single player
	public static void setClientState(ClientState state ) {
		spClientState = state;
	}
	public static ClientState getClientState() {
		return spClientState;
	}
	
	public static void setGame(Game p_game) {
		EngineZildo.game = p_game;
	}
	
	
}