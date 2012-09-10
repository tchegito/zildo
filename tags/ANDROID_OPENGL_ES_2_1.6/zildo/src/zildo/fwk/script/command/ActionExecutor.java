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

package zildo.fwk.script.command;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.client.MapDisplay;
import zildo.client.gui.menu.RegisterChampionMenu;
import zildo.client.sound.BankMusic;
import zildo.client.sound.BankSound;
import zildo.client.stage.SinglePlayer;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.script.xml.element.ActionElement;
import zildo.fwk.ui.UIText;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.Tile;
import zildo.monde.quest.actions.GameOverAction;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.server.EngineZildo;

/**
 * Class splitted from ScriptExecutor, in order to clarify things.
 * <p/>
 * This class has just to render one action.
 * @author tchegito
 */

public class ActionExecutor {

    ScriptExecutor scriptExec;
    int count;
    
    public ActionExecutor(ScriptExecutor p_scriptExec) {
        scriptExec = p_scriptExec;
    }

    /**
     * @param p_action
     * @return boolean
     */
    public boolean render(ActionElement p_action) {
        boolean achieved = false;
        if (p_action.waiting) {
            waitForEndAction(p_action);
            achieved = p_action.done;
        } else {
        	PersoZildo zildo;
            Perso perso = EngineZildo.persoManagement.getNamedPerso(p_action.who);
            if (perso != null) {
                scriptExec.involved.add(perso); // Note that this perso is concerned
            }
            Point location = p_action.location;
            if (p_action.delta && location != null) {
            	Point currentPos = null;
            	if (perso != null) {
            		// Given position is a delta with current one (work ONLY with perso, not with camera)
            		currentPos = new Point(perso.x, perso.y);
            	} else if ("camera".equals(p_action.what)) {
            		currentPos = ClientEngineZildo.mapDisplay.getCamera();
            	} else {
                	Element elem = EngineZildo.spriteManagement.getNamedElement(p_action.what);
                	currentPos = new Point(elem.x, elem.y);
            	}
            	if (currentPos == null) {
            		throw new RuntimeException("We need valid 'who' or 'what' attribute");
            	}
        		location=location.translate(currentPos.x, currentPos.y);
            }
            String text = p_action.text;
            switch (p_action.kind) {
                case pos:
                    if (perso != null) {
                        perso.x = location.x;
                        perso.y = location.y;
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setCamera(location);
                        ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                    } else {
                    	Element elem = EngineZildo.spriteManagement.getNamedElement(p_action.what);
                    	elem.x = location.x;
                    	elem.y = location.y;
                    	// TODO:the 'pushable' attribute shouldn't be set by this default way
                    	elem.setPushable(false);
                    }
                    achieved = true;
                    break;
                case moveTo:
                    if (perso != null) {
                        perso.setGhost(true);
                        perso.setTarget(location);
                        perso.setForward(p_action.backward);
                        perso.setSpeed(p_action.speed);
                        perso.setOpen(p_action.open);
                        perso.setUnstoppable(p_action.unstoppable);
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setTargetCamera(location);
                        ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                    }
                    break;
                case speak:
                	String sentence = UIText.getGameText(text);
                    EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new ScriptAction(sentence));
                    scriptExec.userEndedAction = false;
                    break;
                case script:
                    if (p_action.text != null) {
                        perso.setMouvement(MouvementZildo.valueOf(p_action.text));
                    } else {
	                	MouvementPerso script = MouvementPerso.fromInt(p_action.val);
	                    perso.setQuel_deplacement(script, true);
	                    String param = p_action.fx;
	                    if (param != null) {
		                    switch (script) {
		                    case ZONE:
		        				perso.setZone_deplacement(EngineZildo.mapManagement.range(perso.getX() - 16 * 5, 
		        																	perso.getY() - 16 * 5,
		        																	perso.getX() + 16 * 5, 
		        																	perso.getY() + 16 * 5));
		        				break;
		                    case OBSERVE:
		                    	Perso persoToObserve =  EngineZildo.persoManagement.getNamedPerso(param);
		                    	perso.setFollowing(persoToObserve);
		                    	break;
		                    }
	                    }
                    }
                    achieved = true;
                    break;
                case angle:
                	if (perso.getTarget() != null) {
                		return false;
                	}
                    perso.setAngle(Angle.fromInt(p_action.val));
                    achieved = true;
                    break;
                case wait:
                	count=p_action.val;
                	break;
                case fadeIn:
                	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.fromInt(p_action.val)));
                	break;
                case fadeOut:
                	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_OUT, FilterEffect.fromInt(p_action.val)));
                	break;
                case clear:
                	EngineZildo.askEvent(new ClientEvent(ClientEventNature.CLEAR));
                	achieved=true;
                	break;
                case map:	// Change current map
        			EngineZildo.mapManagement.loadMap(p_action.text, false);
        			ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
                	achieved=true;
                	break;
                case focus:	// Camera focus on given character
                	Element toFocus = perso;
                	if (p_action.what != null) {
                		toFocus = EngineZildo.spriteManagement.getNamedElement(p_action.what);
                	}
                	if (toFocus == null) {
                		ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                	}
                	if (p_action.delta) {
                		Point cameraLoc = new Point(toFocus.x-MapDisplay.CENTER_X, toFocus.y-MapDisplay.CENTER_Y);
                		ClientEngineZildo.mapDisplay.setTargetCamera(cameraLoc);
                	}
                    ClientEngineZildo.mapDisplay.setFocusedEntity(toFocus);
                    // If delta, we go smoothly to the target, except if it's explicitly asked to be unblocking
                    achieved = !p_action.delta || p_action.unblock;
                    break;
                case spawn:	// Spawn a new character
                	if (p_action.who != null) {
                		PersoDescription desc = PersoDescription.valueOf(p_action.text);
                		Perso newOne = EngineZildo.persoManagement.createPerso(desc, location.x, location.y, 0, p_action.who, p_action.val);
                       	newOne.setSpeed(p_action.speed);
                       	newOne.setEffect(p_action.fx);
                       	newOne.initPersoFX();
                        EngineZildo.spriteManagement.spawnPerso(newOne);
                	} else {	// Spawn a new element
                		if (EngineZildo.spriteManagement.getNamedElement(p_action.what) == null) {
                			// Spawn only if doesn't exist yet
	                		SpriteDescription desc = SpriteDescription.Locator.findNamedSpr(p_action.text);
	                		Reverse rev = Reverse.fromInt(p_action.reverse);
	                		Rotation rot = Rotation.fromInt(p_action.rotation);
	                		Element elem = EngineZildo.spriteManagement.spawnElement(desc, location.x, location.y, 0, rev, rot);
	                		elem.setName(p_action.what);
                		}
                	}
                    achieved = true;
                    break;
                case impact:
                	ImpactKind impactKind = ImpactKind.valueOf(p_action.text);
                	EngineZildo.spriteManagement.spawnSprite(new ElementImpact(location.x, location.y, impactKind, null));
                	achieved = true;
                	break;
                case animation:
                	SpriteAnimation anim = SpriteAnimation.valueOf(p_action.text);
                	Element animElem = EngineZildo.spriteManagement.spawnSpriteGeneric(anim, location.x, location.y, 0, null, null);
                	if (p_action.what != null) {
                	    animElem.setName(p_action.what);
                	}
                	achieved = true;
                	break;
                case take:	// Someone takes an item
                	if (p_action.who == null || "zildo".equalsIgnoreCase(p_action.who)) {
                		// This is Zildo
                		zildo=EngineZildo.persoManagement.getZildo();
                		if (p_action.val != 0) {
                			zildo.pickGoodies(null, p_action.val);
                		} else {
                			zildo.pickItem(ItemKind.fromString(text), null);
                		}
                	} else if (perso != null) {
                		// This is somebody else
                		Element elem = EngineZildo.spriteManagement.getNamedElement(p_action.what);
                		perso.addPersoSprites(elem);
                	}
                	achieved=true;
                	break;
                case putDown:	// Zildo loses an item
            		zildo=EngineZildo.persoManagement.getZildo();
            		zildo.removeItem(ItemKind.fromString(text));
            		achieved=true;
            		break;
                case mapReplace:
                	EngineZildo.scriptManagement.addReplacedMapName(p_action.what, text);
                	achieved = true;
                	break;
                case exec:
                	// Note : we can sequence scripts in an action tag.
                	EngineZildo.scriptManagement.execute(text);
                	break;
                case music:
                	if (text == null) { // Stop music ?
	                	EngineZildo.soundManagement.broadcastSound((BankMusic) null, (Point) null);
                	} else {
	                	BankMusic musicSnd=BankMusic.valueOf(text);
	                	EngineZildo.soundManagement.broadcastSound(musicSnd, (Point) null);
                	}
        			EngineZildo.soundManagement.setForceMusic(true);
                	achieved=true;
                	break;
                case sound:
                	BankSound snd=BankSound.valueOf(text);
                	EngineZildo.soundManagement.playSound(snd, null);
                	achieved=true;
                	break;
                case remove:
                	if (p_action.what == null && p_action.who == null) {
                		EngineZildo.persoManagement.clearPersos(false);
                		EngineZildo.spriteManagement.clearSprites(false);
                	} else {
	                	Element toRemove;
	                	if (p_action.what != null) {
	                		toRemove = EngineZildo.spriteManagement.getNamedElement(p_action.what);
	                	} else {
	                		toRemove = perso;
	                    	EngineZildo.persoManagement.removePerso((Perso) toRemove);
	                	}
	                	EngineZildo.spriteManagement.deleteSprite(toRemove);
                	}
                	achieved = true;
                	break;
                case markQuest:
                	if (p_action.val == 1) {
                		EngineZildo.scriptManagement.accomplishQuest(p_action.text, true);
                	} else {
                		EngineZildo.scriptManagement.resetQuest(p_action.text);
                	}
                	achieved=true;
                	break;
                case attack:
                	if (p_action.text != null) {
                		Item weapon = new Item(ItemKind.fromString(text));
                		perso.setWeapon(weapon);
                		perso.attack();
                		achieved=true;
                	}
                	break;
                case activate:
            		Element toActivate = EngineZildo.spriteManagement.getNamedElement(p_action.what);
            		ElementGear gearToActivate = (ElementGear) toActivate;
            		gearToActivate.activate(p_action.activate);
            		break;
                case tile:
                	// Change tile on map
                	Area area = EngineZildo.mapManagement.getCurrentMap();
                	Case c = area.get_mapcase(location.x, location.y+4);
                	if (p_action.back != -1) {
                		c.setBackTile(new Tile(p_action.back, c));
                	}
                	if (p_action.back2 != -1) {
                		c.setBackTile2(new Tile(p_action.back2, c));
                	}
                	if (p_action.fore != -1) {
                		c.setForeTile(new Tile(p_action.fore, c));
                	}
                	EngineZildo.mapManagement.getCurrentMap().set_mapcase(location.x, location.y+4, c);
                	achieved=true;
                	break;
                case filter:
                	switch (p_action.val) {
                	case 0: // REGULAR
                		ClientEngineZildo.ortho.setFilteredColor(new Vector3f(1, 1, 1));
                		break;
                	case 1: // NIGHT
                		ClientEngineZildo.ortho.setFilteredColor(Ortho.NIGHT_FILTER);
                		break;
                	case 2: // SEMI_NIGHT
                		ClientEngineZildo.ortho.setFilteredColor(Ortho.SEMI_NIGHT_FILTER);
                		break;
                	case 3: // RED
                		ClientEngineZildo.mapDisplay.foreBackController.setDisplaySpecific(false, false);
                		ClientEngineZildo.filterCommand.active(CloudFilter.class, false, null);
                		ClientEngineZildo.filterCommand.active(RedFilter.class, true, null);
                		break;
                	}
                	achieved=true;
                	break;
                case end:
                	if (p_action.val == 0) {
                		// Player finished the game !
                		// Register him
               			ClientEngineZildo.client.handleMenu(new RegisterChampionMenu());
                	} else if (p_action.val == 1) {
                		// Game over : player died !
            			EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new GameOverAction());
                	}
            }

            p_action.done = achieved;
            p_action.waiting = !achieved;
        }
        return achieved;
    }

    /**
     * An action has started. Here we're waiting for it to finish.
     * @param p_action
     */
    private void waitForEndAction(ActionElement p_action) {
        String who = p_action.who;
        Perso perso = EngineZildo.persoManagement.getNamedPerso(who);
        boolean achieved = false;
        switch (p_action.kind) {
            case moveTo:
            	if (perso != null) {
	                achieved=perso.hasReachedTarget();
	                if (achieved) {
	                	perso.setTarget(null);
	                }
            	} else if ("camera".equals(p_action.what)) {
            		achieved=ClientEngineZildo.mapDisplay.getTargetCamera() == null;
            	}
                break;
            case focus:
        		achieved=ClientEngineZildo.mapDisplay.getTargetCamera() == null;
            	break;
            case speak:
                achieved = scriptExec.userEndedAction;
                break;
            case wait:
            	achieved = (count-- == 0);
            	break;
            case fadeIn:
            case fadeOut:
           		achieved=ClientEngineZildo.guiDisplay.isFadeOver();
            	break;
            case activate:
        		Element toActivate = EngineZildo.spriteManagement.getNamedElement(p_action.what);
        		ElementGear gearToActivate = (ElementGear) toActivate;
        		achieved=!gearToActivate.isActing();
            	break;
            case exec:
            	achieved=true;
            	break;
        }
        p_action.waiting = !achieved;
        p_action.done = achieved;
    }
}
