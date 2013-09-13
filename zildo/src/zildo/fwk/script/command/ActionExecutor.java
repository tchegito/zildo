/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
import zildo.client.stage.TitleStage;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.LightningFilter;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.logic.IEvaluationContext;
import zildo.fwk.script.logic.SpriteEntityContext;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionElement.ActionKind;
import zildo.fwk.script.xml.element.action.LookforElement;
import zildo.fwk.script.xml.element.action.TimerElement;
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
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.persos.action.ScriptedPersoAction;
import zildo.monde.sprites.persos.ia.BasicMover;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.server.EngineZildo;

/**
 * <p>Class splitted from ScriptExecutor, in order to clarify things.
 * </p>
 * This class has just to render one action.<br/>
 * <br/>
 * <b>PROBLEM</b>:This class has a critical problem, based upon repetition. If more than one script is running the same sequence of actions,
 * we won't be able to distinguish wich process is done or not. Because the 'done' state is marked inside the ActionElement, which is
 * the descriptor AND the statefull script. This should be two distinct objects.
 * 
 * @author tchegito
 */

public class ActionExecutor {

    ScriptExecutor scriptExec;
    int count, nextStep;
    boolean locked;

	final IEvaluationContext context;
	
    public ActionExecutor(ScriptExecutor p_scriptExec, boolean p_locked, IEvaluationContext p_context) {
        scriptExec = p_scriptExec;
        locked = p_locked;
        context = p_context;
    }

    /**
     * @param p_action
     * @return boolean
     */
    //TODO: refactor the 'pos' and 'moveTo' which have a lot in common
    public boolean render(ActionElement p_action) {
        boolean achieved = false;
        if (p_action.waiting) {
            waitForEndAction(p_action);
            achieved = p_action.done;
        } else {
        	PersoZildo zildo;
        	Perso perso;
        	if (context != null && "self".equals(p_action.who)) {
        		// Reserved word : perso himself, in case of a contextual script
        		perso = (Perso) context.getActor();
        	} else {
        		perso = EngineZildo.persoManagement.getNamedPerso(p_action.who);
        	}
        	// Set context for runtime evaluation
        	if (p_action.location != null) {
        		p_action.location.setContext(context);
        	}
            if (perso != null) {
                scriptExec.involved.add(perso); // Note that this perso is concerned
            }
            Point location = null;
            if (p_action.location != null) {
            	location = p_action.location.getPoint();
            }
            if (p_action.delta && location != null) {
            	Point currentPos = null;
            	if (p_action.kind == ActionKind.spawn) {	// Possiblity to spawn relative to zildo
            		currentPos = EngineZildo.persoManagement.getZildo().getCenter();
            	} else if (perso != null) {
            		// Given position is a delta with current one (work ONLY with perso, not with camera)
            		currentPos = new Point(perso.x, perso.y);
            	} else if ("camera".equals(p_action.what)) {
            		currentPos = ClientEngineZildo.mapDisplay.getCamera();
            	} else {
                	SpriteEntity entity = EngineZildo.spriteManagement.getNamedEntity(p_action.what);
                	currentPos = new Point(entity.x, entity.y);
            	}
            	if (currentPos == null) {
            		throw new RuntimeException("We need valid 'who' or 'what' attribute");
            	}
        		location=location.translate(currentPos.x, currentPos.y);
            }
            String text = p_action.text;
            switch (p_action.kind) {
                case pos:
                	float zz = p_action.z.evaluate(context);
                    if (perso != null) {
                    	if (location != null) {
                    		perso.x = location.x;
                    		perso.y = location.y;
                    	}
                    	if (zz != -1) {
                    		perso.z = zz;
                    		perso.az=-0.1f;
                    	}
                        if (p_action.foreground != null) {
                        	perso.setForeground(p_action.foreground);
                        }
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setCamera(location);
                        ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                    } else {
                    	SpriteEntity entity = EngineZildo.spriteManagement.getNamedEntity(p_action.what);
                    	if (location != null) {
                    		entity.x = location.x;
                    		entity.y = location.y;
                    	}
                    	if (zz != -1) {
                    		entity.z = zz;
                    	}
                    	// TODO:the 'pushable' attribute shouldn't be set by this default way
                    	entity.setPushable(false);
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
                        if (p_action.foreground != null) {
                        	perso.setForeground(p_action.foreground);
                        }
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setTargetCamera(location);
                        ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                    } else {
                    	SpriteEntity entity;
                   		entity = EngineZildo.spriteManagement.getNamedEntity(p_action.what);
                    	if (entity != null) {
                    		if ("PHYSIC".equals(p_action.text)) {	// Only works with element
	                    		if (entity.getEntityType().isElement()) {
	                    			Element elem = (Element) entity;
	                    			// Normalize speed vector
	                    			float distance = Point.distance(elem.x, elem.y, location.x, location.y);
	                    			elem.vx = p_action.speed * (location.x - elem.x) / distance;
	                    			elem.vy = p_action.speed * (location.y - elem.y) / distance;
	                    			// calculate z
	                    			float finalT = distance / p_action.speed;
	                    			elem.vz = -(finalT * elem.az) / 2;
	                    			achieved = true;
	                    		}
                    		} else {
                    			entity.setMover(new BasicMover(entity, location.x, location.y));
                    		}
                    	}
                    }
                    break;
                case speak:
                	String sentence = UIText.getGameText(text);
                	if ("synthe".equals(p_action.who)) {
                		// Particular case : incrust some texts on the middle of the screen
                		ClientEngineZildo.client.askStage(new TitleStage(sentence));
                		achieved = true;	// Titlestage is stand-alone
                	} else {
	                    EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new ScriptAction(sentence));
	                    scriptExec.userEndedAction = false;
                	}
                    break;
                case script:
                    if (p_action.text != null) {
                        perso.setMouvement(MouvementZildo.valueOf(p_action.text));
                    } else {
	                	MouvementPerso script = MouvementPerso.fromInt(p_action.val);
	                    perso.setQuel_deplacement(script, true);
	                    String param = p_action.effect;
	                    switch (script) {
	                    case ZONE:
	        				perso.setZone_deplacement(EngineZildo.mapManagement.range(perso.getX() - 16 * 5, 
	        																	perso.getY() - 16 * 5,
	        																	perso.getX() + 16 * 5, 
	        																	perso.getY() + 16 * 5));
	        				break;
	                    case OBSERVE:
		                    if (param != null) {
		                    	Perso persoToObserve =  EngineZildo.persoManagement.getNamedPerso(param);
		                    	perso.setFollowing(persoToObserve);
		                    }
	                    	break;
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
                	SpriteEntity toFocus = perso;
                	if (p_action.what != null) {
                		toFocus = EngineZildo.spriteManagement.getNamedEntity(p_action.what);
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
                	actionSpawn(p_action, location, false);
                	achieved = true;
                    break;
                case impact:
                	ImpactKind impactKind = ImpactKind.valueOf(p_action.text);
                	EngineZildo.spriteManagement.spawnSprite(new ElementImpact(location.x, location.y, impactKind, null));
                	achieved = true;
                	break;
                case animation:
                	SpriteAnimation anim = SpriteAnimation.valueOf(p_action.text);
                	Point loc = new Point(location);
                	if (perso != null) {
                		loc.x+=perso.x;
                		loc.y+=perso.y;
                	}
                	Element animElem = EngineZildo.spriteManagement.spawnSpriteGeneric(anim, loc.x, loc.y, p_action.val, null, null);
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
                case zikReplace:
                	EngineZildo.scriptManagement.addReplacedZikName(p_action.what, text);
                	achieved = true;
                	break;
                case exec:
                	// Note : we can sequence scripts in an action tag.
                	EngineZildo.scriptManagement.execute(text, locked);
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
                	zildo=EngineZildo.persoManagement.getZildo();
                	EngineZildo.soundManagement.playSound(snd, zildo);
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
                	}
            		perso.attack();
            		achieved=true;
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
                	if (p_action.back != -2) {
                		c.setBackTile(p_action.back == -1 ? null : new Tile(p_action.back, c));
                	}
                	if (p_action.back2 != -2) {
                		c.setBackTile2(p_action.back2 == -1 ? null : new Tile(p_action.back2, c));
                	}
                	if (p_action.fore != -2) {
                		c.setForeTile(p_action.fore == -1 ? null : new Tile(p_action.fore, c));
                	}
                	EngineZildo.mapManagement.getCurrentMap().set_mapcase(location.x, location.y+4, c);
                	achieved=true;
                	break;
                case filter:
                	switch (p_action.val) {
                	case 0: // REGULAR
                		ClientEngineZildo.ortho.setFilteredColor(new Vector3f(1, 1, 1));
                		ClientEngineZildo.filterCommand.active(RedFilter.class, false, null);
                		ClientEngineZildo.filterCommand.active(LightningFilter.class, false, null);
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
                	case 4: // LIGHTNING
                		ClientEngineZildo.filterCommand.active(CloudFilter.class, false, null);
                		ClientEngineZildo.filterCommand.active(LightningFilter.class, true, null);
                		break;
                	}
                	achieved = true;
                	break;
                case end:
                	if (p_action.val == 0) {
                		// Player finished the game !
                		// Register him
               			ClientEngineZildo.client.handleMenu(new RegisterChampionMenu());
                	} else if (p_action.val == 1) {
                		// Game over : player died !
            			EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new GameOverAction());
                        scriptExec.userEndedAction = false;
                	}
                	break;
                case respawn:	// Replace Zildo at his previous location
                	EngineZildo.mapManagement.respawn(1);	// 1 HP damage
                	achieved = true;
                	break;
                case visible:
                	if (perso != null) {
                		// Characters are set visible in their own 'animate' overriden method
                		// So we can't just set them invisible, because it would be unefficient.
                		perso.askVisible(p_action.activate);
                	} else if (p_action.what != null) {
                    	Element elem = EngineZildo.spriteManagement.getNamedElement(p_action.what);
                		if (elem != null) {
                			elem.setVisible(p_action.activate);
                		}
                	}
                	achieved = true;
                	break;
                case zoom:
                	if (perso != null) {
                		perso.zoom = p_action.val;
                	}
                	achieved = true;
                	break;
                case herospecial:	// Specific action for hero (to avoid designing too much XML tag)
                	zildo = EngineZildo.persoManagement.getZildo();
                	switch (p_action.val) {
                	case 0:
                		zildo.gainHPWithNecklace();
                		break;
                	case 1:
                		// Block every non-hero characters
                		EngineZildo.spriteManagement.blockNonHero();
                		break;
                	case 2:
                		// Block every non-hero characters
                		EngineZildo.spriteManagement.unblockNonHero();
                		break;
                	case 3:
                		EngineZildo.backUpGame();
                		break;
                	}
                	achieved = true;
                	break;
                case perso:	// Change character attribute (type)
                	if (perso != null) {
                		if (p_action.text != null) {
                			PersoDescription desc = PersoDescription.valueOf(p_action.text);
                			EngineZildo.spriteManagement.deleteSprite(perso);
                			perso = EngineZildo.persoManagement.createPerso(desc, (int) perso.x, (int) perso.y, (int) perso.z, perso.getName(), perso.getAngle().value);
                			EngineZildo.spriteManagement.spawnPerso(perso);
                		}
                		if (p_action.info != null) {
                			perso.setInfo(p_action.info);
                		}
                		if (p_action.effect != null) {
                			perso.setEffect(p_action.effect);
                			perso.initPersoFX();
                		}
                		if (p_action.val != -1) {
                			perso.setAttente(p_action.attente);
                		}
                		if (p_action.action != null) {
                			perso.setAction(new ScriptedPersoAction(perso, p_action.action));
                		}
                	}
                	achieved = true;
                	break;
                case timer:
                	count = 0;
            		nextStep = (int) ((TimerElement)p_action).each.evaluate(context);
                	break;
                case lookFor: // Look for a character around another inside a given radius
                	LookforElement lookFor = (LookforElement) p_action;
                	Perso found = EngineZildo.persoManagement.lookFor(perso, lookFor.radius, p_action.info);
                	if (found != null) {
                		IEvaluationContext persoContext = new SpriteEntityContext(found);
                    	EngineZildo.scriptManagement.execute(lookFor.actions, false, null, false, persoContext);
                	} else {
                		achieved = true;
                	}
                	break;
                case _throw:
                	Element elem = actionSpawn(p_action, location, true);	// Ignore 'who' because it's for the throw
                	location = p_action.target.getPoint();
                	// Turn character in the right direction
                	perso.sight(EngineZildo.persoManagement.getZildo(), true);
                	if ("BELL".equals(p_action.way)) {
            			// Normalize speed vector
            			float distance = Point.distance(elem.x, elem.y, location.x, location.y);
            			elem.vx = p_action.speed * (location.x - elem.x) / distance;
            			elem.vy = p_action.speed * (location.y - elem.y) / distance;
            			// calculate z
            			float finalT = distance / p_action.speed;
            			elem.vz = -(finalT * elem.az) / 2;
                	}
                	elem.setLinkedPerso(perso);
                	elem.flying = true;
                	elem.setAngle(Angle.EST);
        			achieved = true;
                	break;
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
            	} else {
            		SpriteEntity entity = EngineZildo.spriteManagement.getNamedEntity(p_action.what);
            		achieved = (entity != null && entity.getMover() != null && !entity.getMover().isActive());
            	}
                break;
            case focus:
        		achieved=ClientEngineZildo.mapDisplay.getTargetCamera() == null;
            	break;
            case speak:
            case end:
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
            case timer:
            	TimerElement timer = (TimerElement) p_action;
            	if (timer.endCondition != null && timer.endCondition.evaluate(context) == 1) {
            		achieved = true;
                	EngineZildo.scriptManagement.execute(timer.end, false, null, false, context);
            	} else if (count == nextStep) {
            		count = 0;
            		nextStep = (int) timer.each.evaluate(context);
                	EngineZildo.scriptManagement.execute(timer.actions, false, null, false, context);
            	} else {
            		count++;
            	}
            	break;
            case lookFor:
            	LookforElement lookFor = (LookforElement) p_action;
            	int last = lookFor.actions.size() - 1;
            	achieved = lookFor.actions.get(last).done;
            	break;
        }
        p_action.waiting = !achieved;
        p_action.done = achieved;
    }
    
    private float convenientFloatEvaluation(FloatExpression expr) {
    	if (expr == null) {
    		return 0f;
    	} else {
    		return expr.evaluate(context);
    	}
    }

    private Element actionSpawn(ActionElement p_action, Point location, boolean p_ignoreWho) {
    	Element elem = null;
    	if (!p_ignoreWho && p_action.who != null) {
    		PersoDescription desc = PersoDescription.valueOf(p_action.getSpawnType());
    		elem = EngineZildo.persoManagement.createPerso(desc, location.x, location.y, 0, p_action.who, p_action.val);
    		Perso perso = (Perso) elem;
    		perso.setSpeed(p_action.speed);
    		perso.setEffect(p_action.effect);
    		perso.initPersoFX();
            EngineZildo.spriteManagement.spawnPerso(perso);
    	} else {	// Spawn a new element
    		if (EngineZildo.spriteManagement.getNamedElement(p_action.what) == null) {
    			// Spawn only if doesn't exist yet
        		SpriteDescription desc = SpriteDescription.Locator.findNamedSpr(p_action.getSpawnType());
        		Reverse rev = Reverse.fromInt(p_action.reverse);
        		Rotation rot = Rotation.fromInt(p_action.rotation);
        		elem = EngineZildo.spriteManagement.spawnElement(desc, location.x, location.y, 0, rev, rot);
        		elem.setName(p_action.what);
        		if (p_action.effect != null) {
        			elem.setSpecialEffect(EngineFX.valueOf(p_action.effect));
        		}
        		if (p_action.foreground != null) {
        			elem.setForeground(p_action.foreground);
        		}
        		// Physics attributes
        		if (p_action.v != null) {	// Speed
            		elem.vx = convenientFloatEvaluation(p_action.v[0]);
            		elem.vy = convenientFloatEvaluation(p_action.v[1]);
            		elem.vz = convenientFloatEvaluation(p_action.v[2]);
        		}
        		if (p_action.a != null) {	// Acceleration
            		elem.ax = convenientFloatEvaluation(p_action.a[0]);
            		elem.ay = convenientFloatEvaluation(p_action.a[1]);
            		elem.az = convenientFloatEvaluation(p_action.a[2]);
        		}
        		if (p_action.f != null) {	// Friction
            		elem.fx = convenientFloatEvaluation(p_action.f[0]);
            		elem.fy = convenientFloatEvaluation(p_action.f[1]);
            		elem.fz = convenientFloatEvaluation(p_action.f[2]);
        		}
        		if (p_action.z != null) {
        			elem.z = p_action.z.evaluate(context);
        		}
        		if (p_action.alphaA != null) {
        			elem.alphaA = p_action.alphaA.evaluate(context);
        		}
        		if (p_action.shadow != null) {
            		ElementDescription descShadow = (ElementDescription) SpriteDescription.Locator.findNamedSpr(p_action.shadow);
        			elem.addShadow(descShadow);
        		}
    		}
    	}    	
    	return elem;
    }
}
