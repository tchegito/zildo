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

package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.map.Zone;

/**
 * @author Tchegito
 *
 */
public class Gears extends SpriteBanque {

	public Gears() {
		zones=new Zone[] {
				new Zone(104, 144, 16, 32),
				/* Idem à demi ouverte */
				new Zone(120, 144, 16, 32),
				
				/* Grey door without knob */
				new Zone(136, 144, 16, 32),
				new Zone(152, 144, 16, 32),
				
				/* Prison */
				new Zone(176, 0, 8, 32),
				new Zone(184, 0, 8, 5),
				
				/* Boulder on a hill */
				new Zone(88, 160, 16, 20),

				/* Cave - Closed door */
				new Zone(224, 48, 24, 16),
				new Zone(248, 48, 24, 16),	// Master key
				new Zone(272, 48, 24, 16),  // Regular key
				
				/* Explodable wall */
				new Zone(296, 48, 16, 16)
				
				
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
		new GraphChange("interia3", 7, 0)});
	}
}
