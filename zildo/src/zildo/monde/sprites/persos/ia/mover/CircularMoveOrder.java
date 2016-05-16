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

package zildo.monde.sprites.persos.ia.mover;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.SpriteEntityContext;
import zildo.fwk.script.logic.FloatExpression;
import zildo.monde.sprites.elements.Element;
import zildo.monde.util.Pointf;

/**
 * Movement which goes from initial location to given one.
 * 
 * To do so, we describe a circle arc of PI/2 angle.
 * 
 * Note that contrary to PhysicMover, we haven't any placeholder here. We move the linked element directly.
 * 
 * @author Tchegito
 *
 */
public class CircularMoveOrder extends MoveOrder {

	Pointf center;
	Pointf radius;
	double factor;
	double iota;
	int pasX, pasY;
	
	Element mobileElement;
	FloatExpression zoomExpr;
	IEvaluationContext ctx;
	
	final int nbArcs;
	
	final int nbFrames = 100;
	
	/**
	 * Build a circular mover with (x,y) as target coordinate.
	 * @param mobile
	 * @param x
	 * @param y
	 * @complete TRUE=complete circle FALSE=arc of circle (PI/2)
	 */
	public CircularMoveOrder(int x, int y, FloatExpression zoomExpr, boolean complete) {
		super(x, y);
		this.zoomExpr = zoomExpr;
		this.nbArcs = complete ? 4 : 1;
	}
	
	public CircularMoveOrder(int x, int y) {
		this(x, y, null, false);
	}

	@Override
	public Pointf move() {
		double angle = factor * iota;
		mobileElement.vx = (float) ( radius.x * Math.cos(angle) * pasX * factor);
		mobileElement.vy = (float) (-radius.y * Math.sin(angle) * pasY * factor);
		mobileElement.physicMoveWithCollision();
		
		if (zoomExpr != null) {
			mobileElement.zoom = (int) zoomExpr.evaluate(ctx);
			//mobileElement.zoom = 128 + Math.abs((int) (256 * Math.cos(Math.PI / nbFrames * iota)));
		}

		if (iota == nbFrames * nbArcs) {
			target = null;
			active = false;
			mobileElement.vx = 0;
			mobileElement.vy = 0;
		} else {
			iota++;
		}

		return new Pointf(mobile.x, mobile.y);
	}
	
	@Override
	public void init(Mover p_wrapper) {
		super.init(p_wrapper);
		
		if (mobile.getEntityType().isElement() || mobile.getEntityType().isPerso()) {
			mobileElement = (Element) mobile;
		}
		center = new Pointf(target.x, mobile.y);
		radius = new Pointf(Math.abs(target.x - mobile.x), 
							Math.abs(target.y - mobile.y));
		pasX = target.x > mobile.x ? 1 : -1;
		pasY = target.y > mobile.y ? -1 : 1;
		factor = Math.PI/2 / nbFrames;
		iota = 0;
		
		// Context
		if (zoomExpr != null) {
			ctx = new SpriteEntityContext(mobileElement) {
				
				@Override
				public float getValue(String key) {
					if ("bell".equals(key)) {
						return (float) Math.abs(Math.cos(Math.PI / nbFrames * iota));
					}
					return super.getValue(key);
				}

			};
		}
	}
}
