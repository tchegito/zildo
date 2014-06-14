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

package zildo.fwk.script.model.point;

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
	
	public void setContextFromEntity(SpriteEntity entity) { 
		// Default : do nothing (for PointFixed)
	};

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
    	try {
    		return new PointFixed(p_text);
    	} catch (NumberFormatException e) {
    		// Context will be provided later, when needed
    		IPoint pGetter = new PointEvaluator(p_text, null);
    		return pGetter;
    	}
    }
    
	/**
	 * Returns an IPoint object : either fixed, or runtime evaluated.<br/>
	 * @param p_text
	 * @return IPoint
	 */
    public static IPoint fromString(String p_text, SpriteEntity p_entity) {
    	try {
    		return new PointFixed(p_text);
    	} catch (NumberFormatException e) {
    		IEvaluationContext context = new SpriteEntityContext(p_entity);
    		IPoint pGetter = new PointEvaluator(p_text, context);
    		return pGetter;
    	}
    }
}
