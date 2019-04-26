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
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class PointEvaluator extends IPoint {

	FloatExpression exprX, exprY;
	IEvaluationContext context;
	
	public PointEvaluator(String str, IEvaluationContext ctx) {
		context = ctx;
		String[] strCoords = str.split(",");
		exprX = new FloatExpression(strCoords[0]);
		exprY = new FloatExpression(strCoords[1]);
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

}
