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

import zildo.fwk.ZUtils;
import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;

/**
 * Simple interface allowing to return a Point (two integer coordinates).
 * 
 * @author Tchegito
 *
 */
public abstract class IPoint {

	public abstract Point getPoint();

	public void setContext(IEvaluationContext context) { 
		// Default : do nothing (for PointFixed)
	};
	
	/**
	 * Returns an IPoint object : either fixed, or runtime evaluated.<br/>
	 * Note that context is not provided here. But it need to be later.
	 * @param p_text
	 * @return IPoint
	 */
	public static IPoint fromString(String p_text) {
    	if (ZUtils.isNumeric(p_text)) {
    		return new PointFixed(p_text);
    	} else {
    		// Context will be provided later, when needed
    		return new PointEvaluator(p_text, null);
    	}
	}    	

	/**
	 * Returns an IPoint object : either fixed, or runtime evaluated.<br/>
	 * @param p_text
	 * @return IPoint
	 */
    public static IPoint fromString(String p_text, SpriteEntity p_entity) {
    	String[] numbers = p_text.split(",");
    	if (numbers.length == 2 && ZUtils.isNumeric(numbers[0]) && ZUtils.isNumeric(numbers[1])) {
    		return new PointFixed(p_text);
    	} else {
    		IEvaluationContext context = new SpriteEntityContext(p_entity);
    		return new PointEvaluator(p_text, context);
    	}
    }
}
