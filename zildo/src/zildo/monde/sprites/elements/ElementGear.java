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

package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.monde.map.Area;
import zildo.monde.map.ChainingPoint;
import zildo.monde.sprites.desc.GearDescription;
import zildo.monde.sprites.persos.PersoZildo;
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
	public void push(PersoZildo p_perso) {
		if (!acting) {
			GearDescription gearDesc = (GearDescription) desc;
			switch (gearDesc) {
			case GREEN_DOOR:
				int keys = p_perso.getCountKey();
				if (keys != 0) {
					acting = true;
					EngineZildo.soundManagement.broadcastSound(
							BankSound.ZildoUnlock, this);
					p_perso.setCountKey(--keys);

					// Trigger door
					Area map = EngineZildo.mapManagement.getCurrentMap();
					String mapName = map.getName();
					// Get the map coordinates in front of Zildo (with his
					// angle)
					int axx = (int) p_perso.x / 16 + p_perso.angle.coords.x;
					int ayy = (int) p_perso.y / 16 + p_perso.angle.coords.y;
					ChainingPoint ch = map.getCloseChainingPoint(axx, ayy);
					if (ch != null) {
						EngineZildo.scriptManagement.openDoor(mapName, ch);
					}
				}
				break;

			}
		}

	}

	public void activate(boolean value) {
		count=0;
		acting=true;
		activate=value;
	}
	
	@Override
	public void animate() {
		if (acting) {
			GearDescription gearDesc = (GearDescription) desc;
			switch (gearDesc) {
				case GREEN_DOOR:
				case GREEN_DOOR_OPENING:
					switch (count) {
						case 10:
							setDesc(GearDescription.GREEN_DOOR_OPENING);
							EngineZildo.soundManagement.broadcastSound(BankSound.ZildoUnlockDouble, this);
							break;
						case 20:
							dying = true;
					}
					count++;
					break;
				case CAVE_SIMPLEDOOR:
					int pas = activate ? -6 : 6;	// Closing or opening ?
					switch (count) {
					case 20:
						acting=false;
					case 10:
						EngineZildo.soundManagement.broadcastSound(BankSound.ZildoPousse, this);
						y+=pas;
					default:
						break;
					}
					setAjustedX((int)x);
					setAjustedY((int)y);
					count++;
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
}
