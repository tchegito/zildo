/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.fwk.script.xml.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import zildo.client.sound.BankSound;
import zildo.fwk.script.model.ZSCondition;
import zildo.fwk.script.model.ZSSwitch;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.quest.QuestEvent;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Zone;
import zildo.server.EngineZildo;

public class TriggerElement extends AnyElement {

	public final QuestEvent kind;
	String name; // dialog, map, item and questDone
	String mover;
	int numSentence;
	Point location;
	Point tileLocation;
	int radius; // For location
	boolean not;	// Use for inversion of inventory posess
	Zone region; // Unimplemented yet
	boolean immediate;	// TRUE=trigger is disabled just after activation (default:true)
	Angle angle;
	Gear gearType;
	int tileValue;
	
	enum Gear {	// Type of mechanism (for LOCATION trigger only)
		BUTTON,	// Button can be pressed
		TIMED_BUTTON // Button can be pressed and return as action is over (and character is away from the button)
	};
	
	boolean pressed = false;	// For buttons being pressed during the game
	
	List<String> deadPersos;	// Perso expected to be dead
	
	ZSSwitch questSwitch;
	String questName;	// Name of the quest containing this trigger (used for buttons)
	
	public TriggerElement(QuestEvent p_kind) {
		kind = p_kind;
	}

	public TriggerElement(QuestEvent p_kind, String p_questName) {
		this(p_kind);
		questName = p_questName;
	}

	//
	@Override
	public void parse(Element p_elem) {
		if (kind == null) {
			throw new RuntimeException("Trigger kind is unknown !");
		}
		xmlElement = p_elem;
		
		immediate = !"false".equals(readAttribute("immediate"));
		
		switch (kind) {
		case DIALOG:
			numSentence = Integer.valueOf(p_elem.getAttribute("num"));
		case LOCATION:
			name = readAttribute("name");
			mover = readAttribute("mover");
			radius = readInt("radius");
			String gear = readAttribute("gear");
			if (gear != null) {
				gearType = Gear.valueOf(gear);
			}
			location = readPoint("pos");
			tileLocation = readPoint("tilePos");
			tileValue = readInt("tileValue", -1);
			break;
		case PUSH:
			name = readAttribute("name");	// Name of element to push
			String txt =readAttribute("angle");
			angle = Angle.fromInt(Integer.parseInt(txt));
			break;
		case QUESTDONE:
			name = readAttribute("name");
			questSwitch = ZSSwitch.parseForScript(name);
			break;
		case INVENTORY:
			// TODO: replace this by a switch => most complete
			name = p_elem.getAttribute("item");
			not = name.indexOf("!") != -1;
			name = name.replaceAll("!", "");
			break;
		case DEAD:
			String persos = p_elem.getAttribute("who");
			deadPersos = new ArrayList<String>();
			deadPersos.addAll(Arrays.asList(persos.split(",")));
			break;
		}

	}

	/**
	 * Returns TRUE if the given trigger matches the current one.
	 * <p/>
	 * We assume they are same kind, and that given one is undone.
	 * 
	 * @param p_another
	 * @return boolean
	 */
	public boolean match(TriggerElement p_another) {
		if (kind != p_another.kind) {
			return false; // Different kinds
		}
		switch (kind) {
		case DIALOG:
			if (p_another.name.equals(name)
					&& p_another.numSentence == numSentence) {
				return true;
			}
			break;
		case INVENTORY:
			if (p_another.name.equals(name)) {
				return !not;
			}
			break;
		case LOCATION:
			if (p_another.name.equals(name)) {
				if (p_another.location == null && tileValue == -1 && mover == null && location == null && tileLocation == null) {
					return true;
				} else if (tileValue != -1) {
					return tileValue == p_another.tileValue;
				} else if (mover != null) {
					return mover.equals(p_another.mover);
				} else if (tileLocation != null && p_another.location != null) {
					int gridX = p_another.location.x / 16;
					int gridY = p_another.location.y / 16;
					boolean onIt = tileLocation.x == gridX && tileLocation.y == gridY;
					if (onIt && !pressed) {
						if (gearType != null) {
							switch (gearType) {
							case TIMED_BUTTON:
								EngineZildo.mapManagement.getCurrentMap().addSpawningTile(tileLocation, questName, 0, false);
							case BUTTON:
								EngineZildo.soundManagement.broadcastSound(BankSound.Switch, location);
								EngineZildo.mapManagement.getCurrentMap().writemap(gridX,  gridY, 256 * 3+212);
							}
						}
						pressed = true;
						return true;
					} else if (!onIt) {
						pressed = false;
					}
					return false;
				} else if (p_another.location != null && location != null) {
					float dist = p_another.location.distance(location);
					// System.out.println(dist + "   <   "+(8f + 16*radius));
					return dist < (8f + 16 * radius);
				}
			}
			break;
		case QUESTDONE:
			if (questSwitch.contains(p_another.name)) {
				return questSwitch.evaluate().equals(ZSCondition.TRUE);
			}
			break;
		case DEAD:
			deadPersos.remove(p_another.name);
			return deadPersos.size() == 0;
		case PUSH:
			boolean nameGood = name.equals(p_another.name);
			boolean angleGood = angle == null || angle == p_another.angle;
			return nameGood && angleGood;
		}
		return false;
	}

	public boolean isLocationSpecific() {
		return kind == QuestEvent.LOCATION && name != null && (location != null || tileLocation != null || mover != null || tileValue != -1);
	}

	/**
	 * The 'done' member isn't reliable, because of the QUESTDONE kind of triggers.
	 * 
	 * @return boolean
	 */
	public boolean isDone() {
		switch (kind) {
		case QUESTDONE:
			return questSwitch.evaluate().equals( ZSCondition.TRUE );
		case INVENTORY:
			PersoZildo zildo = EngineZildo.persoManagement.getZildo();
			return zildo != null && zildo.hasItem(ItemKind.fromString(name)) == !not;
		case LOCATION:
			Area map = EngineZildo.mapManagement.getCurrentMap();
			if (map != null && !isLocationSpecific() && name.equals(EngineZildo.mapManagement.getCurrentMap().getName())) {
				return true;
			}
		default:
			return done;
		}
	}

	/**
	 * Ingame method to check a dialog trigger.
	 * 
	 * @param p_name
	 * @param p_num
	 * @return TriggerElement for Dialog
	 */
	public static TriggerElement createDialogTrigger(String p_name, int p_num) {
		TriggerElement elem = new TriggerElement(QuestEvent.DIALOG);
		elem.name = p_name;
		elem.numSentence = p_num;
		return elem;
	}

	/**
	 * Ingame method to check a inventory trigger.
	 * 
	 * @param p_name
	 * @return TriggerElement for Inventory
	 */
	public static TriggerElement createInventoryTrigger(ItemKind p_item) {
		TriggerElement elem = new TriggerElement(QuestEvent.INVENTORY);
		elem.name = p_item.toString();
		return elem;
	}

	/**
	 * Ingame method to check a location trigger.
	 * 
	 * @param p_mapName
	 * @param p_location (not mandatory)
	 * @param p_mover name of the moving platform (not mandatory)
	 * @param p_tileValue tile value (=nBank * 256 + nIndex)
	 * @return TriggerElement
	 */
	public static TriggerElement createLocationTrigger(String p_mapName,
			Point p_location, String p_mover, int p_tileValue) {
		TriggerElement elem = new TriggerElement(QuestEvent.LOCATION);
		elem.name = p_mapName;
		if (p_location != null) {
			elem.location = p_location;
		}
		if (p_mover != null) {
			elem.mover = p_mover;
		}
		elem.tileValue = p_tileValue;
		return elem;
	}

	/**
	 * Ingame method to check a 'quest done' trigger.
	 * 
	 * @param p_quest
	 * @return TriggerElement
	 */
	public static TriggerElement createQuestDoneTrigger(String p_quest) {
		TriggerElement elem = new TriggerElement(QuestEvent.QUESTDONE);
		elem.name = p_quest;
		return elem;
	}

	/**
	 * Ingame method to check a set of persos death.
	 * @param p_name
	 * @return TriggerElement
	 */
	public static TriggerElement createDeathTrigger(String p_name) {
		TriggerElement elem = new TriggerElement(QuestEvent.DEAD);
		elem.name = p_name;
		return elem;
	}

	/**
	 * Ingame method to check an object being pushed
	 * @param p_name
	 * @param p_angle
	 * @return TriggerElement
	 */
	public static TriggerElement createPushTrigger(String p_name, Angle p_angle) {
		TriggerElement elem = new TriggerElement(QuestEvent.PUSH);
		elem.angle = p_angle;
		elem.name = p_name;
		return elem;
	}
	
	public String getName() {
		return name;
	}

	public void reset() {
		// Set all fields like it was at the beginning
		parse(xmlElement);
		done = false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(kind.toString());
		sb.append(" ");
		if (name != null) {
			sb.append(name).append(" ");
		}
		if (location != null) {
			sb.append(location).append(" ");
		}
		if (kind == QuestEvent.DIALOG) {
			sb.append(numSentence);
		}
		return sb.toString();
	}
	
	public boolean isImmediate() {
		return immediate;
	}
}