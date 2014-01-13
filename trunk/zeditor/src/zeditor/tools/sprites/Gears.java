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

package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class Gears extends SpriteBanque {

	public Gears() {
		zones=new Zone[] {
				new Zone(0, 505, 16, 32),
				/* Idem à demi ouverte */
				new Zone(16, 505, 16, 32),
				
				/* Grey door without knob */
				new Zone(32, 505, 16, 32),
				new Zone(48, 505, 16, 32),
				
				/* Prison */
				new Zone(64, 505, 8, 32),
				new Zone(72, 505, 8, 5),
				
				/* Boulder on a hill */
				new Zone(80, 505, 16, 20),

				/* Cave - Closed door */
				new Zone(96, 505, 24, 16),
				new Zone(120, 505, 24, 16),	// Master key
				new Zone(144, 505, 24, 16),  // Regular key
				
				
				new Zone(168, 505, 24, 16),	// Regular key opening

				/* Explodable wall */
				new Zone(192, 505, 16, 16),
				
				/* Big blue door */
				new Zone(0, 538, 24, 20),
				
				/* Broken walls */
				new Zone(0, 491, 10, 13), new Zone(11, 491, 10, 13),
				
				/* Grate */
				new Zone(24, 537, 13, 28)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
		new GraphChange("interia3", 7, 0)});
	}
}
