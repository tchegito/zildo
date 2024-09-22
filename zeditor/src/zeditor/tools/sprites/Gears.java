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

				/* Cave - Closed door (7)*/
				new Zone(96, 505, 24, 16),
				new Zone(120, 505, 24, 16),	// Master key
				new Zone(144, 505, 24, 16),  // Regular key
				
				
				new Zone(168, 505, 24, 16),	// Regular key opening

				/* Explodable wall (11)*/
				new Zone(192, 505, 16, 16),
				
				/* Big blue door */
				new Zone(0, 538, 24, 20),
				
				/* Broken walls */
				new Zone(0, 491, 10, 13), new Zone(11, 491, 10, 13),
				
				/* Grate */
				new Zone(24, 537, 13, 28), new Zone(37, 537, 13, 28),
				
				/* Hidden door */
				new Zone(51, 538, 19, 24), new Zone(71, 538, 19, 24),
			
				/* Lava artefacts */
				new Zone(2, 215, 31, 19), new Zone(3, 235, 29, 14), new Zone(1, 250, 32, 24), new Zone(1, 275, 68, 27),

				/* Palace door (23)*/
				new Zone(0, 192, 16, 26), new Zone(16, 192, 16, 26)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[]{
				new GraphChange("interia3", 0, 0),
				new GraphChange("interia7", 19, 0),
				new GraphChange("palace1", 23, 0)});
	}
}
