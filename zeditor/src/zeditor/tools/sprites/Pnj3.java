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
		    new Zone(238, 235, 14, 14)
		};
		
		pkmChanges = Arrays.asList(new GraphChange[] { new GraphChange("pnj2", 0, 0), 
				});
	}
}
