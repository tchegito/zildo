/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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
import zildo.monde.map.Point;
import zildo.monde.sprites.desc.GearDescription;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class ElementGear extends Element {

	boolean acting = false;
	int count = 0;

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

	@Override
	public void animate() {
		if (acting) {
			switch (count) {
			case 10:
				setDesc(GearDescription.GREEN_DOOR_OPENING);
				EngineZildo.soundManagement.broadcastSound(
						BankSound.ZildoUnlockDouble, this);
				break;
			case 20:
				dying = true;
			}
			count++;
		}
	}

	@Override
	public Point getCenter() {
		super.getCenter();
		center.y = (int) y - sprModel.getTaille_y();
		return center;
	}
}
