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

package zildo.fwk.gfx.filter;

import zildo.Zildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Point;

/**
 * Draws a circle around a specific center (Zildo !).<br/>
 * 
 * Mathematical explanation:<p/>
 * 
 * Each point respecting following equation must be shown :<br/>
 * (x - a)² + (y - b)² > r² <p/>
 * 
 * So the screen is divided vertically into 3 areas :<ol>
 * <li>before circle : all is black</li>
 * <li>circle : each line has two roots from an equation derived from the first one
 * <li>after circle : all is black again</li>
 * </ol>
 * 
 * In order to calculate the derived equation, let's set a variable Y = (y - b)² <br/>
 * So we have: <br/>
 * (x - a)² + Y > r² <br/>
 * wich gives : <br/>
 * x² - 2ax + a² + Y - r² > 0 <p/>
 * 
 * If we calculate the delta : <br/>
 * D = (-2a)² - 4 * (a² + Y - r²) <br/>
 * D = 4 * (a² - r²)<p>
 * 
 * So we deduce the 2 roots of the equation : <br/>
 * x1 = (2 * a - sqrt(D)) / 2 <br/>
 * x2 = (2 * a + sqrt(D)) / 2 <br/>
 * @author Tchegito
 *
 */
public abstract class CircleFilter extends FadeScreenFilter {

	/**
	 * @param graphicStuff
	 */
	public CircleFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	// Center of the circle
	public Point center = new Point(Zildo.viewPortX / 2, Zildo.viewPortY / 2);
	
	// Coeff to get the circle drawn all over the screen
	protected double coeffLevel = Math.sqrt(Math.pow(Zildo.viewPortX, 2) + Math.pow(Zildo.viewPortY, 2)) / 255f;
		
	public void setCenter(int p_x, int p_y) {
		center.x = p_x;
		center.y = p_y;
	}
}
