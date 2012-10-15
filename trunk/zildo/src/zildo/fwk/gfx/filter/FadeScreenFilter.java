/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.fwk.gfx.filter;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;

/**
 * Screen filter which can fade in and out.
 * 
 * @author Tchegito
 *
 */
public abstract class FadeScreenFilter extends ScreenFilter {
	
	/**
	 * @param graphicStuff
	 */
	public FadeScreenFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	/**
	 * @return Fade level (0..255)
	 */
	final public int getFadeLevel() {
		return ClientEngineZildo.filterCommand.getFadeLevel();
	}
}
