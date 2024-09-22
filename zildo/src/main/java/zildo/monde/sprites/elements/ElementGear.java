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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.desc.GearDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class ElementGear extends Element {

	boolean acting = false;	// Is this gear moving ?
	int count = 0;
	boolean activate;	// is this gear asked to turn on/off ?
	Boolean state=null;	// TRUE=open / FALSE=close
	
	public ElementGear(int p_x, int p_y) {
		super();
		x = p_x;
		y = p_y;
	}

	/**
	 * Push this gear element.
	 * 
	 * @param p_perso
	 *            Character pushing this gear. (useful for doors, we need to
	 *            know if character has keys to do so)
	 */
	public void push(PersoPlayer p_perso) {
		if (!acting) {
			GearDescription gearDesc = (GearDescription) desc;
			switch (gearDesc) {
			case GREEN_DOOR:
			case CAVE_KEYDOOR:
			case CAVE_MASTERDOOR:
				int keys = p_perso.getCountKey();
				boolean openBySpecificKey = false;
				ItemKind keyItem = null;
				// Name of gear object can indicate the need of specific item
				if (name != null && name.startsWith("item")) {
					String itemStr = name.substring(4);
					keyItem = ItemKind.fromString(itemStr);
					openBySpecificKey = p_perso.hasItem(keyItem);
					// This door must be opened by a specific key. But if hero doesn't have it ==> nothing happens
					if (!openBySpecificKey) {
						break;
					}
				}
				if (keys > 0 || openBySpecificKey) {
					// Get the map coordinates in front of Zildo (with his angle)
					int axx = (int) p_perso.x / 16 + p_perso.angle.coords.x;
					int ayy = (int) p_perso.y / 16 + p_perso.angle.coords.y;
					Area map = EngineZildo.mapManagement.getCurrentMap();
					ChainingPoint ch = map.getCloseChainingPoint(axx, ayy);
					if (ch != null) {
						acting = true;
						EngineZildo.soundManagement.broadcastSound(BankSound.ZildoUnlock, this);
						if (openBySpecificKey) {
							p_perso.removeItem(keyItem);
						} else {
							p_perso.setCountKey(--keys);
						}
	
						// Trigger door
						String mapName = map.getName();
						EngineZildo.scriptManagement.openDoor(mapName, ch);
					}
				}
			default:
				break;

			}
		}

	}

	public void activate(boolean value) {
		if (state == null || value != state) {
			count=0;
			acting=true;
			activate=value;
			
			state = value;
		}
	}
	
	@Override
	public void animate() {
		if (acting) {
			GearDescription closed = null, opened = null;
			GearDescription gearDesc = (GearDescription) desc;
			switch (gearDesc) {
				case GREEN_DOOR:
				case GREEN_DOOR_OPENING:
				case CAVE_KEYDOOR:
				case CAVE_KEYDOOR_OPENING:
				case CAVE_MASTERDOOR:
					opened = gearDesc.getOpeningDesc();
					switch (count) {
						case 10:
							setDesc(opened);
							EngineZildo.soundManagement.broadcastSound(BankSound.ZildoUnlockDouble, this);
							break;
						case 20:
							dying = true;
					}
					count++;
					break;
				case GREEN_SIMPLEDOOR:
				case GREEN_SIMPLEDOOR_OPENING:
				case PALACE_DOOR:
				case PALACE_DOOR_OPENING:
				case GRATE:
				case GRATE_OPENING:
				case HIDDENDOOR:
				case HIDDENDOOR_OPENING:
					closed = gearDesc.getClosedDesc();
					opened = gearDesc.getOpeningDesc();
					if (state) { // Opening
						switch (count) {
						case 10:
							setDesc(opened);
							EngineZildo.soundManagement.broadcastSound(BankSound.ZildoUnlockDouble, this);
							break;
						case 20:
							setVisible(false);
							acting = false;	// Notify action is over for ActionExecutor
						}
					} else { // Closing
						switch (count) {
						case 10:
							setDesc(opened);
							EngineZildo.soundManagement.broadcastSound(BankSound.ZildoUnlockDouble, this);
							setVisible(true);
							break;
						case 20:
							setDesc(closed);
							acting = false;
							break;
						}
					}
					count++;
					break;
				case CAVE_SIMPLEDOOR:
					int pas = activate ? -6 : 6;	// Closing or opening ?
					if (reverse == Reverse.VERTICAL || rotation == Rotation.UPSIDEDOWN) {
						pas = -pas;
					}
					int pasX = 0;
					int pasY = pas;
					switch (rotation) {
					case CLOCKWISE:
						pasX = -pasY;
						pasY = 0;
						break;
					case COUNTERCLOCKWISE:
						pasX = pasY;
						pasY = 0;
					default:
						break;
					}
					switch (count) {
					case 20:
						acting=false;
						if (activate && !rotation.isWidthHeightSwitched()) { z+=32; y+=32; }	// For vertical opened door put there above characters to correctly render
					case 10:
						EngineZildo.soundManagement.broadcastSound(BankSound.ZildoPousse, this);
						x+=pasX;
						y+=pasY;
						Perso perso = EngineZildo.persoManagement.collidePerso((int)x, getCenter().y, this, 6);
						if (perso != null && !perso.isZildo()) {
							// A character will be blocked by the gear => push him with same vector
							perso.x+=pasX;
							perso.y+=pasY;
						}
					default:
						break;
					}
					setAjustedX((int)x);
					setAjustedY((int)y);
					count++;
				default:
					break;
			}
		}
	}

	@Override
	public Point getCenter() {
		super.getCenter();
		center.y = (int) y - sprModel.getTaille_y();
		return center;
	}
	
	public boolean isActing() {
		return acting;
	}
	
	public boolean isOpen() {
		return !(state == null) && state;
	}
	
	@Override
	public boolean isSolid() {
		if (Boolean.TRUE == state) {
			return false;
		} else {
			return true;
		}
	}
}
