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

package zeditor.tools.sprites;

import java.util.Arrays;

import zeditor.tools.tiles.GraphChange;
import zildo.monde.util.Zone;

/**
 * @author Tchegito
 *
 */
public class Pnj3 extends SpriteBanque {

	public Pnj3() {
		zones=new Zone[] {
				
		    // Falcor
		    new Zone(162, 235, 16, 21), new Zone(147, 235, 15, 22),
		    new Zone(178, 235, 13, 24), new Zone(192, 235, 14, 25),
		    new Zone(222, 235, 16, 22), new Zone(206, 235, 16, 23),
		    new Zone(238, 235, 14, 14),
		    // Bramble
		    new Zone(253, 237, 16, 16),
            // Flying serpent
		    /*
		    new Zone(122, 313, 27, 25),
		    new Zone(122, 313, 27, 25),
		    new Zone(122, 313, 27, 25),
		    */
            //new Zone(34, 265, 9, 15), new Zone(43, 265, 9, 15), new Zone(52, 265, 9, 14),
		    new Zone(34, 265, 14, 15), new Zone(48, 265, 14, 15), new Zone(62, 265, 14, 15),
            // Fisher
		    new Zone(147, 261, 18, 23), new Zone(165, 261, 16, 24),
		    new Zone(181, 261, 14, 24), new Zone(195, 261, 14, 24),
		    // Fish
		    new Zone(294, 21, 19, 8), new Zone(294, 30, 19, 8),
		    new Zone(294, 39, 19, 10), new Zone(295, 50, 20, 10)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("pnj2", 0, 0), 
				
				});
	}
}
