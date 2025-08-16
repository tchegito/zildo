/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.client.MapDisplay;
import zildo.client.gui.menu.RegisterChampionMenu;
import zildo.client.sound.BankMusic;
import zildo.client.sound.BankSound;
import zildo.client.stage.TitleStage;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.Ortho;
import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.EarthQuakeFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.LightningFilter;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionKind;
import zildo.fwk.script.xml.element.action.ListenElement;
import zildo.fwk.script.xml.element.action.LookforElement;
import zildo.fwk.script.xml.element.action.LoopElement;
import zildo.fwk.script.xml.element.action.TimerElement;
import zildo.fwk.script.xml.element.action.runtime.RuntimeAction;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.map.Case;
import zildo.monde.map.ChainingPoint;
import zildo.monde.map.Tile;
import zildo.monde.quest.actions.GameOverAction;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.FontDescription;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteAnimation;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.CustomizableElementChained;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGear;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;
import zildo.monde.sprites.elements.ElementImpact;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.ControllablePerso;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.persos.action.ScriptedPersoAction;
import zildo.monde.sprites.persos.ia.PathFinderArc;
import zildo.monde.sprites.persos.ia.mover.BasicMoveOrder;
import zildo.monde.sprites.persos.ia.mover.CircularMoveOrder;
import zildo.monde.sprites.persos.ia.mover.EasinMoveOrder;
import zildo.monde.sprites.persos.ia.mover.PhysicMoveOrder;
import zildo.monde.sprites.persos.ia.mover.StraightMoveOrder;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.sprites.utils.MouvementZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;
import zildo.monde.util.Vector3f;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptCall;

/**
 * <p>Class splitted from ScriptExecutor, in order to clarify things.
 * </p>
 * This class has just to render one action.<br/>
 * <br/>
 * 
 * @author tchegito
 */

public class ActionExecutor extends RuntimeExecutor {

    ScriptExecutor scriptExec;
    boolean locked;
    boolean uniqueAction;	// TRUE means unique action is executing (useful for timer)
	
    public ActionExecutor(ScriptExecutor p_scriptExec, boolean p_locked, IEvaluationContext p_context, boolean p_uniqueAction, ScriptProcess p_caller) {
		super(p_caller);
		scriptExec = p_scriptExec;
		locked = p_locked;
		context = p_context;
		uniqueAction = p_uniqueAction;
	}

	/**
	 * @param p_action
	 * @return boolean
	 */
	// TODO: refactor the 'pos' and 'moveTo' which have a lot in common
	public boolean render(RuntimeAction p_runtimeAction) {
		boolean achieved = false;
		ActionElement p_action = (ActionElement) p_runtimeAction.action;
		if (p_runtimeAction.waiting) {
			waitForEndAction(p_runtimeAction);
			achieved = p_runtimeAction.done;
		} else {
			PersoPlayer zildo;
			Perso perso = getNamedPerso(p_action.who);
			// Set context for runtime evaluation
			if (p_action.location != null) {
				p_action.location.setContext(context);
			}
			if (p_action.target != null) {
				p_action.target.setContext(context);
			}
			if (perso != null) {
                scriptExec.involved.add(perso); // Note that this perso is concerned
			}
			Pointf location = null;
			if (p_action.location != null) {
				location = new Pointf(p_action.location.getPoint());
				if (!p_action.delta) {
					// If we don't use scrollOffset, stairs script fails (example:natureb1)
					location.add(EngineZildo.mapManagement.getCurrentMap().getScrollOffset().multiply(16));
				}
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
					SpriteEntity entity = getNamedEntity(p_action.what);
					if (entity != null) { // Add for Issue 74, to understand how
											// 'what' can't be found
						currentPos = new Point(entity.x, entity.y);
					}
				}
				if (currentPos == null) {
					throw new RuntimeException(
							"We need valid 'who' or 'what' attribute in action "
									+ p_action);
				}
				location = location.translate(currentPos.x, currentPos.y);
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
						perso.az = -0.1f;
					}
					if (p_action.foreground != null) {
						perso.setForeground(p_action.foreground);
					}
					perso.beingMoved();
				} else if ("camera".equals(p_action.what)) {
					ClientEngineZildo.mapDisplay.setCamera(location.toPoint());
					ClientEngineZildo.mapDisplay.setFocusedEntity(null);
				} else {
					SpriteEntity entity = getNamedEntity(p_action.what);
					if (location != null) {
						entity.setAjustedX((int) (entity.getAjustedX() + location.x - entity.x));
						entity.setAjustedY((int) (entity.getAjustedY() + location.y - entity.y));
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
					int zzz = (int) p_action.z.evaluate(context);
					if (zzz != -1) {
						perso.setTargetZ(zzz);
					}
					perso.setForward(p_action.backward);
					perso.setSpeed(p_action.speed);
					perso.setOpen(p_action.open);
					perso.setUnstoppable(p_action.unstoppable);
					if (p_action.foreground != null) {
						perso.setForeground(p_action.foreground);
					}
					if (p_action.deltaFloor > 0) {
	                	// Need to update floor BEFORE movement if higher, because hero would be hidden otherwise
						int newFloor = perso.floor + p_action.deltaFloor;
						Area area = EngineZildo.mapManagement.getCurrentMap();
						int cx = (int) (location.x / 16);
						int cy = (int) (location.y / 16);
	                		if (newFloor <= area.getHighestFloor() && area.readmap(cx, cy, false, newFloor) != null) {
       							// Check if there's really a tile at upper floor at this place
							perso.setFloor(newFloor);
						}
					}
					if ("arc".equals(p_action.text)) {
						perso.setPathFinder(new PathFinderArc(perso));
					}
				} else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setTargetCamera(location.toPoint());
					ClientEngineZildo.mapDisplay.setFocusedEntity(null);
				} else { // Element/entity
					List<SpriteEntity> entities = getNamedEntities(p_action.what);
					for (SpriteEntity entity : entities) {
						if ("physic".equals(p_action.text)) { // Only works with
																// element
							entity.setMover(new PhysicMoveOrder(location.x,
									location.y));
						} else if ("arc".equals(p_action.text)) {
							entity.setMover(new CircularMoveOrder(location.x, location.y, p_action.zoom, false));
						} else if ("circular".equals(p_action.text)) {
							entity.setMover(new CircularMoveOrder(location.x, location.y, p_action.zoom, true, p_action.functionAngleX, p_action.functionAngleY));
						} else if ("easein".equals(p_action.text)) {
							entity.setMover(new EasinMoveOrder(location.x, location.y, p_action.speed));
						} else if ("straight".equals(p_action.text)) {
							entity.setMover(new StraightMoveOrder(location.x, location.y, p_action.speed));
						} else {
							entity.setMover(new BasicMoveOrder(location.x,
									location.y, p_action.speed));
						}
					}
					achieved = p_action.unblock;
				}
				break;
			case speak:
				if ("synthe".equals(p_action.who)) {
                		// Particular case : incrust some texts on the middle of the screen
					ClientEngineZildo.client.askStage(new TitleStage(text));
					achieved = true; // Titlestage is stand-alone
				} else {
	                EngineZildo.dialogManagement.launchDialog(EngineZildo.getClientState(), null, new ScriptAction(text, p_action.who));
					scriptExec.userEndedAction = false;
				}
				break;
			case script:
				if (p_action.text != null) {
					perso.setMouvement(MouvementZildo.valueOf(p_action.text));
                    } else if (perso != null) {	// Perso may have died during the script
	                	MouvementPerso script = MouvementPerso.fromInt(p_action.val);
					String param = p_action.effect;
					switch (script) {
					case ZONE:
					case ZONEARC:
						int size = param == null ? 5 : Integer.valueOf(param);
	        				perso.setZone_deplacement(EngineZildo.mapManagement.range(perso.getX() - 16 * size, 
	        																	perso.getY() - 16 * size,
	        																	perso.getX() + 16 * size, 
										perso.getY() + 16 * size));
						break;
					case OBSERVE:
					case FOLLOW:
					case CHAIN_FOLLOW:
						if (param != null) {
							Perso p = getNamedPerso(param);
							if (p != null) {
								perso.setFollowing(p);
							} else {
								SpriteEntity entity = getNamedEntity(param);
			                    	if (entity != null && !entity.getEntityType().isEntity()) {
									Element elemToObserve = (Element) entity;
									perso.setFollowing(elemToObserve);
								}
							}
						}
					default:
						break;
					}
					perso.setQuel_deplacement(script, true);
				}
				achieved = true;
				break;
			case angle:
				// We allow NULL perso here (see Issue 110)
				if (perso == null || perso.getTarget() != null) {
					return false;
				}
				perso.setAngle(Angle.fromInt(p_action.val));
				achieved = true;
				break;
			case wait:
				p_runtimeAction.count = p_action.val;
				break;
			case fadeIn:
				EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN,
						FilterEffect.fromInt(p_action.val)));
				break;
			case fadeOut:
				EngineZildo.askEvent(new ClientEvent(
						ClientEventNature.FADE_OUT, FilterEffect
								.fromInt(p_action.val)));
				break;
			case clear:
				EngineZildo.askEvent(new ClientEvent(ClientEventNature.CLEAR));
				achieved = true;
				break;
			case map: // Change current map
				EngineZildo.mapManagement.loadMap(p_action.text, false);
				ClientEngineZildo.mapDisplay
						.setCurrentMap(EngineZildo.mapManagement
								.getCurrentMap());
				achieved = true;
				break;
			case focus: // Camera focus on given character
				SpriteEntity toFocus = perso;
				if (p_action.what != null) {
					toFocus = getNamedEntity(p_action.what);
				}
				if (toFocus == null) {
					ClientEngineZildo.mapDisplay.setFocusedEntity(null);
				}
				if (p_action.delta) {
					Point cameraLoc = new Point(
							toFocus.x - MapDisplay.CENTER_X, toFocus.y
									- MapDisplay.CENTER_Y);
					ClientEngineZildo.mapDisplay.setTargetCamera(cameraLoc);
				}
				ClientEngineZildo.mapDisplay.setFocusedEntity(toFocus);
				// If delta, we go smoothly to the target, except if it's
				// explicitly asked to be unblocking
				achieved = !p_action.delta || p_action.unblock;
				break;
			case spawn: // Spawn a new character or element
				actionSpawn(p_action, location.toPoint(), false, null);
				achieved = true;
				break;
			case animation:
				SpriteAnimation anim = SpriteAnimation.valueOf(p_action.text);
				Point loc = location.toPoint();
				if (perso != null) {
					loc.x += perso.x;
					loc.y += perso.y;
				}
				Element animElem = EngineZildo.spriteManagement
						.spawnSpriteGeneric(anim, loc.x, loc.y, 1,
								p_action.val, null, null);
				if (p_action.what != null) {
					animElem.setName(p_action.what);
				}
				achieved = true;
				break;
			case take: // Someone takes an item
				if (p_action.who == null
						|| "zildo".equalsIgnoreCase(p_action.who)) {
					// This is Zildo
					zildo = EngineZildo.persoManagement.getZildo();
					if (p_action.val != 0) {
						zildo.pickGoodies(null, p_action.val);
					} else {
						ItemKind kind = ItemKind.fromString(text);
						if (kind.canBeInInventory()) { // Item ?
							zildo.pickItem(kind, null);
						} else { // or goodies ?
							ElementDescription desc = (ElementDescription) kind.representation;
							// If this provokes a ClassCastException, that means
							// that XML script is wrong !
							ElementGoodies elem = (ElementGoodies) desc
									.createElement();
							zildo.pickGoodies(elem, 0);
						}
					}
				} else if (perso != null) {
					// This is somebody else
					Element elem = getNamedElement(p_action.what);
					perso.addPersoSprites(elem);
				}
				achieved = true;
				break;
			case putDown: // Zildo loses an item
				zildo = EngineZildo.persoManagement.getZildo();
				zildo.removeItem(ItemKind.fromString(text));
				achieved = true;
				break;
			case mapReplace:
				EngineZildo.scriptManagement.addReplacedMapName(p_action.what,
						text);
				achieved = true;
				break;
			case zikReplace:
				EngineZildo.scriptManagement.addReplacedZikName(p_action.what,
						text);
				achieved = true;
				break;
			case nameReplace:
				EngineZildo.scriptManagement.addReplacedPersoName(p_action.who,
						text);
				achieved = true;
				break;
			case exec:
				exec(text, p_action.unblock);
				break;
			case stop:
				EngineZildo.scriptManagement.stopScene(text);
				achieved = true;
				break;
			case music:
				BankMusic musicSnd = null;
				if (text != null) { // Stop music ?
					musicSnd = BankMusic.valueOf(text);
				}
				EngineZildo.soundManagement.broadcastSound(musicSnd,
						(Point) null);
				EngineZildo.soundManagement.setForceMusic(true);
				achieved = true;
				break;
			case sound:
				if (text == null) {
					ClientEngineZildo.soundPlay.stopLooping();
				} else {
					BankSound snd = BankSound.valueOf(text);
					if (location != null) {
						EngineZildo.soundManagement.playSound(snd,
								(int) location.x, (int) location.y,
								!p_action.activate);
					} else if (snd != null) {
						zildo = EngineZildo.persoManagement.getZildo();
						EngineZildo.soundManagement.playSound(snd, zildo,
								!p_action.activate);
					}
				}
				achieved = true;
				break;
			case remove:
				if (p_action.text != null) { // Remove a chaining point
					Area area = EngineZildo.mapManagement.getCurrentMap();
					ChainingPoint ch = area
							.getNamedChainingPoint(p_action.text);
					if (ch != null) { // Doesn't crash if chaining point can't
										// be found
						area.removeChainingPoint(ch);
					}
				} else if (p_action.way != null) {
					// Remove every character of given type
					PersoDescription desc = PersoDescription
							.valueOf(p_action.way);
					if (desc != null) {
						List<Perso> persos = EngineZildo.persoManagement
								.getTypedPerso(desc);
						for (Perso p : persos) {
							EngineZildo.spriteManagement.deleteSprite(p);
						}
					}
				} else if (p_action.what == null && p_action.who == null) {
					EngineZildo.persoManagement.clearPersos(false);
					EngineZildo.spriteManagement.clearSprites(false);
					EngineZildo.scriptManagement.clearUnlockingScripts();
				} else {
					List<SpriteEntity> toRemove = new ArrayList<>();
					if (p_action.what != null) {
						// It was "getNamedElement" before I need it to remove
						// bunch of leaves
						toRemove = getNamedEntities(p_action.what);
					} else {
						toRemove.add(perso);
					}
					for (SpriteEntity remo : toRemove) {
						EngineZildo.spriteManagement.deleteSprite(remo);
					}
				}
				achieved = true;
				break;
			case markQuest:
				if (p_action.val == 1) {
					EngineZildo.scriptManagement.accomplishQuest(p_action.text,
							true);
				} else {
					EngineZildo.scriptManagement.resetQuest(p_action.text);
				}
				achieved = true;
				break;
			case attack:
				if (p_action.text != null) {
					Item weapon = new Item(ItemKind.fromString(text));
					perso.setWeapon(weapon);
				}
				perso.attack();
				achieved = true;
				break;
			case activate:
				Element toActivate = getNamedElement(p_action.what);
				ElementGear gearToActivate = (ElementGear) toActivate;
				gearToActivate.activate(p_action.activate);
				break;
			case tile:
				// Change tile on map
				Area area = EngineZildo.mapManagement.getCurrentMap();
				Case c = area.get_mapcase((int) location.x, (int) location.y);
				Reverse rev = p_action.reverse == null ? Reverse.NOTHING
						: reverseFromAction(p_action);
				if (p_action.back != -2) {
					c.setBackTile(p_action.back == -1 ? null : new Tile(
							p_action.back, rev, c));
				}
				if (p_action.back2 != -2) {
					c.setBackTile2(p_action.back2 == -1 ? null : new Tile(
							p_action.back2, rev, c));
				}
				if (p_action.fore != -2) {
					c.setForeTile(p_action.fore == -1 ? null : new Tile(
							p_action.fore, rev, c));
				}
				if (p_action.action != null) {
					EngineZildo.scriptManagement.runTileAction(
							p_action.location.getPoint(), p_action.action,
							!p_action.unblock);
				}
				achieved = true;
				break;
			case filter:
				switch (p_action.val) {
				case 0: // REGULAR
					ClientEngineZildo.ortho.setFilteredColor(new Vector3f(1, 1,
							1));
					ClientEngineZildo.filterCommand.active(RedFilter.class,
							false, null);
					ClientEngineZildo.filterCommand.active(
							LightningFilter.class, false, null);
					ClientEngineZildo.filterCommand.active(
							EarthQuakeFilter.class, false, null);
					break;
				case 1: // NIGHT
					ClientEngineZildo.ortho
							.setFilteredColor(Ortho.NIGHT_FILTER);
					break;
				case 2: // SEMI_NIGHT
					ClientEngineZildo.ortho
							.setFilteredColor(Ortho.SEMI_NIGHT_FILTER);
					break;
				case 3: // RED
					ClientEngineZildo.mapDisplay.foreBackController
							.setDisplaySpecific(false, false);
					ClientEngineZildo.filterCommand.active(CloudFilter.class,
							false, null);
					ClientEngineZildo.filterCommand.active(RedFilter.class,
							true, null);
					break;
				case 4: // LIGHTNING
					ClientEngineZildo.filterCommand.active(CloudFilter.class,
							false, null);
					ClientEngineZildo.filterCommand.active(
							LightningFilter.class, true, null);
					break;
				case 5: // EARTH QUAKE
					ClientEngineZildo.filterCommand.active(
							EarthQuakeFilter.class, true, null);
					break;
				}
				achieved = true;
				break;
			case end:
				if (p_action.val == 0) {
					// Player finished the game !
					// Register him
					ClientEngineZildo.client
							.handleMenu(new RegisterChampionMenu());
				} else if (p_action.val == 1) {
					// Game over : player died !
					EngineZildo.dialogManagement.launchDialog(
							EngineZildo.getClientState(), null,
							new GameOverAction());
					scriptExec.userEndedAction = false;
				}
				break;
			case respawn: // Replace Zildo at his previous location
				EngineZildo.mapManagement.respawn(true, 1); // 1 HP damage
				achieved = true;
				break;
			case visible:
				if (perso != null) {
					// Characters are set visible in their own 'animate'
					// overriden method
					// So we can't just set them invisible, because it would be
					// unefficient.
					perso.askVisible(p_action.activate);
				} else if (p_action.what != null) {
					SpriteEntity entity = getNamedEntity(p_action.what);
					if (entity != null) {
						entity.setVisible(p_action.activate);
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
			case herospecial: // Specific action for hero (to avoid designing
								// too much XML tag)
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
					// Unblock every non-hero characters
					EngineZildo.spriteManagement.unblockNonHero();
					break;
				case 3:
					EngineZildo.backUpGame();
					break;
				case 4:
					// Replace hero at the last backed up game and update his HP
					int heroPv = Math.max(1, zildo.getPv()); // At least 1 HP
					EngineZildo.mapManagement.deleteCurrentMap();
					Client client = ClientEngineZildo.getClientForMenu();
					client.quitGame();
					EngineZildo.restoreBackedUpGame();
					// A circlefilter is planned to redisplay properly the scene
					ClientEngineZildo.filterCommand.active(
							BilinearFilter.class, false, null);
					ClientEngineZildo.filterCommand.active(CircleFilter.class,
							true, null);
					EngineZildo.mapManagement.respawn(false, 0);
					EngineZildo.persoManagement.getZildo().setPv(heroPv);
					EngineZildo.backUpGame();
					break;
				case 5:
					// Damage hero
					int damage = p_action.text == null ? 1 : Integer
							.valueOf(p_action.text);
					zildo.beingWounded(null, damage);
					zildo.stopBeingWounded();
					break;
				case 6:
					zildo.setAppearance(ControllablePerso.PRINCESS_BUNNY);
					break;
				case 7:
					zildo.setAppearance(ControllablePerso.ZILDO);
					break;
				case 8: // Set count of nettle leaves
					int count = Integer.valueOf(p_action.text);
					zildo.setCountNettleLeaf(count);
					break;
				case 9:
					EngineZildo.mapManagement.arrangeLocation(zildo);
					break;
				case 10: // Alert location
					Point alertPos = Point.fromString(p_action.text);
					EngineZildo.mapManagement.getCurrentMap().alertAtLocation(
							alertPos);
					break;
				case 11:
					Perso p = EngineZildo.persoManagement
							.getNamedPerso(p_action.text);
					p.setAlerte(true);
					break;
				}
				achieved = true;
				break;
			case sprite: // Change element attribute
				List<SpriteEntity> entities = getNamedEntities(p_action.what);
				if (!entities.isEmpty()) {
					for (SpriteEntity entity : entities) {
						if (p_action.text != null) {
							entity.setDesc(SpriteDescription.Locator
									.findNamedSpr(p_action.text));
						}
						if (p_action.reverse != null) {
							entity.reverse = reverseFromAction(p_action);
						}
						if (p_action.z != null) {
							entity.z = p_action.z.evaluate(context);
						}
						if (entity.getEntityType().isElement()) {
							applyCommonAndPhysicAttributes((Element) entity,
									p_action);
						}
						if (p_action.targetScene != null
								|| p_action.fallScene != null) {
							Element elem = (Element) entity;
							// Resolve variables from context
							elem.targetScene = p_action.targetScene;
							elem.fallScene = new ScriptCall(p_action.fallScene,
									context).resolveVariables();
						}
					}
				}

				achieved = true;
				break;
			case perso: // Change character attribute (type)
				if (perso != null) {
					if (p_action.text != null) {
						PersoDescription desc = PersoDescription
								.valueOf(p_action.text);
						EngineZildo.spriteManagement.deleteSprite(perso);
						perso = EngineZildo.persoManagement.createPerso(desc,
								(int) perso.x, (int) perso.y, (int) perso.z,
								perso.getName(), perso.getAngle().value);
						EngineZildo.spriteManagement.spawnPerso(perso);
					}
					if (p_action.info != null) {
						perso.setInfo(p_action.info);
					}
					if (p_action.effect != null) {
						perso.setEffect(p_action.effect);
						perso.initPersoFX();
					}
					if (p_action.attente != -1) {
						perso.setAttente(p_action.attente);
					}
					if (p_action.reverse != null) {
						perso.reverse = reverseFromAction(p_action);
					}
					if (p_action.rotation != -1) {
						perso.rotation = Rotation.fromInt(p_action.rotation);
					}
					if (p_action.parent != null) {
						// Link character to their parent
						Perso persoParent = getNamedPerso(p_action.parent);
						persoParent.addPersoSprites(perso);
					}
					if (p_action.speed != 0) {
						perso.setSpeed(p_action.speed);
					}
					if (p_action.action != null) {
						if (p_action.action.length() == 0) {
							EngineZildo.scriptManagement.stopPersoAction(perso);
							perso.setAction(null);
							// perso.setGhost(false);
						} else {
							if (EngineZildo.scriptManagement.isPersoActing(perso)) {
								EngineZildo.scriptManagement.stopPersoAction(perso);
							}
							perso.setAction(new ScriptedPersoAction(perso,
									p_action.action, context));
						}
					}
					if (p_action.weapon != null) {
						// TODO: not very clever ! setActiveWeapon and setWeapon
						// should merge. So as
						// guardWeapon and weapon attributes.
						((PersoNJ) perso).setActiveWeapon(GuardWeapon
								.valueOf(p_action.weapon));
					}
					int addSpr = (int) p_action.addSpr.evaluate(context);
					if (addSpr != -1) {
						perso.setAddSpr(addSpr);
						// Reset potential previous sprite sequence
						if (!perso.isZildo()) {
							((PersoNJ) perso).setSpriteSequence(new int[] {}, 0);
						}
					}
					if (p_action.pv != -1) {
						perso.setPv(p_action.pv);
					}
					if (p_action.z != null) {
						perso.z = p_action.z.evaluate(context);
					}
					if (p_action.flag != -1) {
						perso.setFlagBehavior(p_action.flag);
					}
					applyCommonAndPhysicAttributes((Element) perso, p_action);
				}
				achieved = true;
				break;
			case seqPerso:
				((PersoNJ) perso).setSpriteSequence(p_action.sequence, p_action.val);
				achieved = true;
				break;
			case timer:
				if (!uniqueAction) {
					// If the timer is inside a script, run it as a single one,
					// to avoid locking problems
					executeSubProcessInParallel(Collections
							.singletonList((LanguageElement) p_action));
					achieved = true;
				} else {
					p_runtimeAction.count = 0;
					// On timer's first run, don't wait <each> frames but
					// execute actions at once
					p_runtimeAction.nextStep = 0; // (int)
													// ((TimerElement)p_action).each.evaluate(context);
				}
				break;
			case loop: // 'end' condition is checked at the end of a loop
						// execution
			case _for:
				executeSubProcess(((LoopElement) p_action).actions);
				break;
			case lookFor: // Look for a character/element around another inside
							// a given radius
				LookforElement lookFor = (LookforElement) p_action;
				SpriteEntity found;
				if (perso == null && location != null) {
					// If no character is specified, we could look around a
					// given location anyway
					perso = new PersoNJ(-2);
					perso.x = location.x;
					perso.y = location.y;
				}
				if (lookFor.desc != null) {
					ElementDescription desc = ElementDescription
							.valueOf(lookFor.desc);
					found = EngineZildo.spriteManagement.lookFor(perso,
							lookFor.radius, desc, lookFor.sight);
				} else {
					found = EngineZildo.persoManagement.lookForOne(perso,
							lookFor.radius, p_action.info, lookFor.sight);
				}
				if (found != null ^ lookFor.negative) { // XOR !
					IEvaluationContext lookForContext = context;
					if (found != null && lookFor.changeContext) {
						lookForContext = new SpriteEntityContext(found, context);
					}
					// Specificity here: we create a subprocess with a different
					// context: upon found character
					executeSubProcess(lookFor.actions, lookForContext);
				} else {
					achieved = true;
				}
				break;
			case listen: // Hear if an alert is done around character
				Point here = null;
				if (perso != null) {
					here = new Point(perso.x, perso.y);
				} else if (location != null) {
					here = location.toPoint();
				}
				if (EngineZildo.mapManagement.getCurrentMap()
						.isAnAlertAtLocation(here.x, here.y)) {
					ListenElement listen = (ListenElement) p_action;
					executeSubProcessInParallel(listen.actions);
				}
				achieved = true;
				break;
			case _throw:
				if (perso != null) { // If thrower has been killed => don't throw NPE
					Element elem = actionSpawn(p_action, location.toPoint(), true, perso); // Ignore 'who' because it's for the throw
					location = new Pointf(p_action.target.getPoint());
					// Turn character in the right direction
					perso.sight(EngineZildo.persoManagement.getZildo(), true);
					// Normalize speed vector
					float distance = Point.distance(elem.x, elem.y, location.x,	location.y);
					elem.vx = p_action.speed * (location.x - elem.x) / distance;
					elem.vy = p_action.speed * (location.y - elem.y) / distance;
					if ("BELL".equals(p_action.way)) {
						// calculate z
						float finalT = distance / p_action.speed;
						elem.vz = -(finalT * elem.az) / 2;
					}
					if (elem.getDesc() == ElementDescription.FIRE_BALL) {
						// Reverse fireball accordingly to its target
						if (elem.vx < 0) {
							elem.reverse = Reverse.HORIZONTAL;
						}
						if (elem.vy < 0) {
							elem.reverse = elem.reverse.flipVertical();
						}
					}
					elem.setLinkedPerso(perso);
					elem.flying = true;
					elem.setAngle(Angle.fromDelta(elem.vx, elem.vy));
				}
				achieved = true;
			default:
				break;
			}

			p_runtimeAction.done = achieved;
			p_runtimeAction.waiting = !achieved;
		}
		return achieved;
	}

	/**
	 * An action has started. Here we're waiting for it to finish.
	 * 
	 * @param p_action
	 */
	private void waitForEndAction(RuntimeAction p_runtimeAction) {
		ActionElement p_action = (ActionElement) p_runtimeAction.action;
		String who = p_action.who;
		Perso perso = getNamedPerso(who);
		boolean achieved = false;
		switch (p_action.kind) {
		case moveTo:
			String sceneToTrigger = null;
			if (perso != null) {
				achieved = (perso.getTarget() == null && perso.getTargetZ() == null)
						|| perso.hasReachedTarget();
				// Allow character to skip his movement if he's unable to reach it (with 'skippable' at true)
				if (perso.getDelta().isEmpty() && p_action.skippable) {
					if (p_runtimeAction.count == 10) {
						achieved = true;
					} else {
						p_runtimeAction.count++;
					}
				}
				if (achieved) {
					// Make sure character is EXACTLY on desired target
					if (perso.getTarget() != null) {
						perso.x = perso.getTarget().x;
						perso.y = perso.getTarget().y;
					}
					perso.setPos_seqsprite(0);
					perso.setTarget(null);
					perso.setTargetZ(null);
					if (p_action.deltaFloor < 0) {
						// Need to update floor AFTER movement if lower
						int newFloor = perso.getFloor() + p_action.deltaFloor;
						if (newFloor >= 0
								&& newFloor < Constantes.TILEENGINE_FLOOR) {
							// Try to reach higher/lower floor if exists
							Area area = EngineZildo.mapManagement
									.getCurrentMap();
							int cx = (int) perso.x / 16;
							int cy = (int) perso.y / 16;
							if (area.readmap(cx, cy, false, newFloor) != null) {
								// Lower : we check if there's really a tile at
								// lower floor at this place
								perso.setFloor(newFloor);
							}
						}
					}
					sceneToTrigger = perso.getTargetScene();
				}
			} else if ("camera".equals(p_action.what)) {
				achieved = ClientEngineZildo.mapDisplay.getTargetCamera() == null;
			} else {
				SpriteEntity entity = getNamedEntity(p_action.what);
				achieved = entity == null
						|| (entity != null && entity.getMover() != null && !entity
								.getMover().isActive());
				if (achieved && entity != null)
					sceneToTrigger = entity.getTargetScene();
			}
			// Trigger a scene if any is configured
			if (sceneToTrigger != null) {
				exec(sceneToTrigger, true);
			}
			break;
		case focus:
			achieved = ClientEngineZildo.mapDisplay.getTargetCamera() == null;
			break;
		case speak:
		case end:
			achieved = scriptExec.userEndedAction;
			break;
		case wait:
			achieved = (p_runtimeAction.count-- <= 0);
			break;
		case fadeIn:
		case fadeOut:
			achieved = ClientEngineZildo.filterCommand.isFadeOver();
			break;
		case activate:
			Element toActivate = getNamedElement(p_action.what);
			ElementGear gearToActivate = (ElementGear) toActivate;
			// Important: unblock is managed HERE, to avoid blocking mapscript
			// conditions
			achieved = !gearToActivate.isActing() || p_action.unblock
					|| !locked;
			break;
		case exec:
			// Wait for subscript to be over (in theory, this check is useful
			// ONLY WHEN an unlocking scene (for example, persoAction) is
			// calling
			// a subscript, and we want to wait for its end before going
			// forward.
			achieved = true; // p_action.unblock; // ||
								// !scriptExec.isProcessing(p_action.text);
			break;
		case timer:
			TimerElement timer = (TimerElement) p_action;
			if (timer.endCondition != null
					&& timer.endCondition.evaluate(context) == 1) {
				achieved = true;
				executeSubProcessInParallel(timer.end);
			} else if (p_runtimeAction.count == p_runtimeAction.nextStep) {
				p_runtimeAction.count = 0;
				p_runtimeAction.nextStep = (int) timer.each.evaluate(context);
				executeSubProcessInParallel(timer.actions);
			} else {
				p_runtimeAction.count++;
			}
			achieved |= p_action.unblock;
			break;
		case loop:
		case _for:
			LoopElement loop = (LoopElement) p_action;
			if (loop.whileCondition.evaluate(context) == 1) {
				// Restart
				executeSubProcess(loop.actions);
			} else {
				achieved = true;
			}
			break;
		case lookFor:
			achieved = true;
		default:
			break;
		}
		p_runtimeAction.waiting = !achieved;
		p_runtimeAction.done = achieved;
	}

	private float convenientFloatEvaluation(FloatExpression expr) {
		if (expr == null) {
			return 0f;
		} else {
			return expr.evaluate(context);
		}
	}

	/**
	 * All getter with naming entity/element/perso goes here, to handle local
	 * variable
	 **/
	private SpriteEntity getNamedEntity(String name) {
		return EngineZildo.spriteManagement
				.getNamedEntity(getVariableName(name));
	}
	
	private List<SpriteEntity> getNamedEntities(String name) {
		return EngineZildo.spriteManagement
				.getNamedEntities(getVariableName(name));
	}

	private Element getNamedElement(String name) {
		return EngineZildo.spriteManagement
				.getNamedElement(getVariableName(name));
	}

	private Perso getNamedPerso(String name) {
		return EngineZildo.persoManagement.getNamedPersoInContext(
				getVariableName(name), context);
	}

	/**
	 * Spawn an element with given parameters in action. Paramater "ignoreWho"
	 * is when we're spawning an element to be thrown by "action.who". So "who"
	 * becomes irrelevant.
	 * 
	 * @return spawned element
	 */
	private Element actionSpawn(ActionElement p_action, Point location, boolean p_ignoreWho, Perso issuer) {
		Element elem = null;
		String name = null;
		if (!p_ignoreWho && p_action.who != null) {
			name = handleLocalVariable(p_action.who);
			if (getNamedPerso(name) == null) {
				// Spawn the character only if no one with the same name exists yet
				PersoDescription desc = PersoDescription.valueOf(p_action.getSpawnType());
				elem = EngineZildo.persoManagement.createPerso(desc, location.x, location.y, 0, name, p_action.val);
				Perso perso = (Perso) elem;
				perso.setSpeed(p_action.speed);
				perso.setEffect(p_action.effect);
				perso.setFloor((int) p_action.floor.evaluate(context));
				perso.setAddSpr((int) p_action.addSpr.evaluate(context));
				if (p_action.info != null) {
					perso.setInfo(p_action.info);
				}
				if (p_action.pv != -1) {
					perso.setPv(p_action.pv);
				}
				if (p_action.reverse != null) {
					perso.reverse = reverseFromAction(p_action);
				}
				perso.initPersoFX();
				if (p_action.weapon != null) {
					// TODO: not very clever ! setActiveWeapon and setWeapon should merge. So as
					// guardWeapon and weapon attributes.
					((PersoNJ) perso).setActiveWeapon(GuardWeapon.valueOf(p_action.weapon));
				}
				if (p_action.z != null) {
					perso.z = p_action.z.evaluate(context);
				}
				if (p_action.carried != null) {
        			// Set item carried by the spawned character (dropped when he dies)
        			ElementDescription carriedDesc = ElementDescription.safeValueOf(p_action.carried.evaluate());
					if (carriedDesc != null) {
						perso.setCarriedItem(carriedDesc);
					}
				}
				EngineZildo.spriteManagement.spawnPerso(perso);
			}
		} else { // Spawn a new element
			name = handleLocalVariable(p_action.what);
			if (isLocal(name) || getNamedElement(p_action.what) == null) {
				// Spawn only if doesn't exist yet
				SpriteEntity entity = null;
				Rotation rot = Rotation.fromInt(p_action.rotation == -1 ? 0
						: p_action.rotation);
				Reverse rev = p_action.reverse == null ? Reverse.NOTHING
						: reverseFromAction(p_action);
				if (p_action.impact != null) {
					ImpactKind impactKind = ImpactKind.valueOf(p_action.impact);
    				elem = new ElementImpact(location.x, location.y, impactKind, null);
					elem.reverse = rev;
					entity = elem;
				} else {
	        		SpriteDescription desc = SpriteDescription.Locator.findNamedSpr(p_action.getSpawnType());
					// Is this inside a chest ?
					int ax = location.x / 16;
					int ay = location.y / 16;
					Area area = EngineZildo.mapManagement.getCurrentMap();
					int tileDesc = area.readmap(ax, ay);
					if (Tile.isOpenedChest(tileDesc)) {
	        			// Nothing to display: item is already taken from the chest
	        		} else if (Tile.isLinkableToItem(tileDesc) && Tile.isClosedChest(tileDesc)) {
						area.setCaseItem(ax, ay, desc.getNSpr(), name);
					} else {
						elem = null;
						// Chained
						if (ElementDescription.isPlatform(desc)) {
							entity = EngineZildo.spriteManagement.createSprite(
									desc, location.x, location.y,
									Boolean.TRUE == p_action.foreground, rev,
									false);
							if (entity.getEntityType().isElement()) {
								elem = (Element) entity;
							}
						} else {
		        			elem = EngineZildo.spriteManagement.createElement(desc, location.x, location.y, 0, rev, rot, issuer);
							entity = elem;
							if (desc instanceof FontDescription) {
								// Particular case=> animation on GUI
								entity.setDesc(desc);
								entity.setEntityType(EntityType.FONT);
							}
						}
					}
				}
				if (entity != null) {
					if (p_action.z != null) {
						entity.z = p_action.z.evaluate(context);
					}
					if (p_action.chainCount > 0) {
	        			// With chained elements, we must not spawn the "matrix" element. It will be spawned by
						// ChainedElement#animate()
						if (elem == null) {
	        				throw new RuntimeException("Unable to spawn a chain of non element !");
						}
	        			Element chain = new CustomizableElementChained(elem, p_action.chainCount, (int) p_action.chainDelay.evaluate(context));
						EngineZildo.spriteManagement.spawnSprite(chain);
						entity = chain;
						elem = chain;
					} else {
						// Really spawn it
						EngineZildo.spriteManagement.spawnSprite(entity);
					}

					entity.setFloor((int) p_action.floor.evaluate(context));
					entity.rotation = rot;
					entity.setName(name);
					if (p_action.effect != null) {
	        			entity.setSpecialEffect(EngineFX.valueOf(p_action.effect));
					}

					if (p_action.foreground != null) {
						entity.setForeground(p_action.foreground);
					}
					if (elem != null) { // Element specific
						if (p_action.shadow != null && !p_action.shadow.equals("null")) {
		            		ElementDescription descShadow = (ElementDescription) SpriteDescription.Locator.findNamedSpr(p_action.shadow);
							elem.addShadow(descShadow);
						}
					}
				}

			}
		}
		// Enable for both element and character
		if (elem != null) {
			applyCommonAndPhysicAttributes(elem, p_action);
		}
		return elem;
	}

    /** Apply v(x,y,z), a(x,y,z), and f(x,y,z) on given element, assumed as not null. **/
    private void applyCommonAndPhysicAttributes(Element elem, ActionElement p_action) {
		// Physics attributes
		if (p_action.v != null) { // Speed
			elem.vx = convenientFloatEvaluation(p_action.v[0]);
			elem.vy = convenientFloatEvaluation(p_action.v[1]);
			elem.vz = convenientFloatEvaluation(p_action.v[2]);
		}
		if (p_action.a != null) { // Acceleration
			elem.ax = convenientFloatEvaluation(p_action.a[0]);
			elem.ay = convenientFloatEvaluation(p_action.a[1]);
			elem.az = convenientFloatEvaluation(p_action.a[2]);
		}
		if (p_action.f != null) { // Friction
			elem.fx = convenientFloatEvaluation(p_action.f[0]);
			elem.fy = convenientFloatEvaluation(p_action.f[1]);
			elem.fz = convenientFloatEvaluation(p_action.f[2]);
		}
		if (p_action.alphaA != null) {
			elem.alphaA = p_action.alphaA.evaluate(context);
		}
		if (p_action.alphaV != null) {
			elem.alphaV = p_action.alphaV.evaluate(context);
		}
		if (p_action.alpha != null) {
			float fAlpha = p_action.alpha.evaluate(context);
			if (fAlpha != -1) {
				elem.setAlpha(fAlpha);
			}
		}
		if (p_action.zoom != null) {
			elem.zoom = (int) p_action.zoom.evaluate(context);
		}
		if (p_action.zoomV != null) {
			elem.zoomV = (int) p_action.zoomV.evaluate(context);
		}
		if (p_action.light != -1) {
			elem.light = p_action.light;
		}
		// If element has not-null speed and high z, declare it as 'flying' one (see Element#beingThrown)
		if (!elem.flying && elem.getEntityType() == EntityType.ELEMENT && (elem.vx != 0 || elem.vy != 0) && elem.z >= 4) {
			elem.flying = true;
			elem.setAngle(Angle.fromDelta(elem.vx, elem.vy));
			elem.relativeZ = EngineZildo.mapManagement.getCurrentMap().readAltitude((int) elem.x / 16, (int) elem.y / 16);
			SpriteDescription dd = elem.getDesc();
			ElementDescription desc = dd != null && dd instanceof ElementDescription ? (ElementDescription) dd : null;
			if (!elem.hasShadow() && (desc == null || desc.hasShadow()) && !"null".equals(p_action.shadow)) {
				elem.addShadow(ElementDescription.SHADOW_SMALL);
			}
		}
	}

	public void terminate() {
    	// We don't have to terminate, if this script has called a new one (lookFor, timer, actions...) : context should be preserved !
    	// TODO:See if it's ok, but previous remarks isn't taken into account anymore. We removed the condition.
    	if (/** !uniqueAction && **/ context != null) {
    		// Unregister each variable name, because it only existed in this executor scope => wipe out
			context.terminate();
		}
		// System.out.println("Variables size:"+EngineZildo.scriptManagement.getVariables().size());
	}

	private void exec(String text, boolean unblock) {
		// Note : we can sequence scripts in an action tag.
		// If 'unblock' attribute is set on 'exec' action, given scene won't
		// lock the game
		String sceneName = getVariableValue(text); // Check for 'loc:...' as
													// scene name
		ScriptProcess whosTheCaller = caller;
		if (unblock) {
			whosTheCaller = null;
		}
		EngineZildo.scriptManagement.execute(sceneName, locked && !unblock,
				context, whosTheCaller);
	}

	private Reverse reverseFromAction(ActionElement p_action) {
		return Reverse.fromInt(p_action.reverse.evaluateInt(context));
	}
}
