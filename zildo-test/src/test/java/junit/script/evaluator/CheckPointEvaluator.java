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

package junit.script.evaluator;

import org.junit.Assert;

import org.junit.Test;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.model.point.IPoint;
import zildo.fwk.script.model.point.PointEvaluator;
import zildo.fwk.script.model.point.PointFixed;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.util.Point;

/**
 * @author Tchegito
 *
 */
public class CheckPointEvaluator {

	@Test
	public void checkFixed() {
		String strLocation = "15,16";
		IPoint pGetter = new PointFixed(strLocation);
		Point p = pGetter.getPoint();
		Assert.assertTrue("X should have been 15 instead of "+p.x, p.x == 15);
		Assert.assertTrue("Y should have been 16 instead of "+p.y, p.y == 16);
	}

	@Test
	public void checkEvaluator() {
		String strLocation = "x+15, 4-y*2";
		SpriteEntity entity = new SpriteEntity(160, 100, false);
		IEvaluationContext context = new SpriteEntityContext(entity);
		IPoint pGetter = new PointEvaluator(strLocation, context);
		Point p = pGetter.getPoint();
		Assert.assertTrue("X should have been 175 instead of "+p.x, p.x == 175);
		Assert.assertTrue("Y should have been -196 instead of "+p.y, p.y == -196);
	}
	
	@Test
	public void voidCheckAssignation() {
		SpriteEntity entity = new SpriteEntity(160, 100, false);
		IPoint pGetter = IPoint.fromString("x+15, 7", entity);
		Assert.assertTrue(pGetter.getClass().equals(PointEvaluator.class));
		
		pGetter = IPoint.fromString("12,168", entity);
		Assert.assertTrue(pGetter.getClass().equals(PointFixed.class));
	}
}
