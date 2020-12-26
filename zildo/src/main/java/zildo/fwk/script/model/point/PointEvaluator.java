/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.fwk.script.model.point;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

/**
 * Several expression are allowed:
 * <ul>
 * <li>"(37, 45)" with any immediate values separated with comma</li>
 * <li>"(zildo.x, loc:i)" with any variables from context</li>
 * <li>"(1532.0)" with single float value, whose X and Y components will be extracted with bitwise operation</li>
 * </ul>
 * @author Tchegito
 *
 */
public class PointEvaluator extends IPoint {

	FloatExpression exprX, exprY;
	IEvaluationContext context;
	
	public PointEvaluator(String str, IEvaluationContext ctx) {
		context = ctx;
		String[] strCoords = str.split(",");
		if (strCoords.length == 1) {	// Expression is just one variable name
			exprX = new FloatExpression(str + " % 1024");
			exprY = new FloatExpression(str + " / 1024");
		} else {
			exprX = new FloatExpression(strCoords[0]);
			exprY = new FloatExpression(strCoords[1]);
		}
	}
		
	@Override
	public void setContext(IEvaluationContext p_context) {
		context = p_context;
	}
	
	@Override
	public Point getPoint() {
		float x = exprX.evaluate(context);
		float y = exprY.evaluate(context);
		return new Point(x, y);
	}

	/** Convert a String which is a float which is a pair into that pair, to get a Point.
	 *  
	 * To quote a Patrick Rondat's title: 'why do you do things like that ?' **/
	public static Point fromStrFloat(String strFloat) {
		return fromFloat(Float.parseFloat(strFloat));
	}
	
	public static Point fromFloat(float f) {
		float x = (int)f & 1023;
		float y = (int)f >> 10;
		return new Point(x, y);
	}
	
	
	public static float toSingleFloat(Point p) {
		return (float) ((int)p.x | ((int)p.y << 10));
	}
	
	public static float toSingleFloat(Pointf p) {
		return (float) ((int)p.x | ((int)p.y << 10));
	}
}
