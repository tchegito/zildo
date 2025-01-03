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
import zildo.monde.sprites.desc.EntityType;
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
	FloatExpression functionX;	// Can be defined in fx="..." using 'alpha' as available angle in cos/sin functions
	FloatExpression functionY;
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
	public CircularMoveOrder(float x, float y, FloatExpression zoomExpr, boolean complete, FloatExpression functionX, FloatExpression functionY) {
		super(x, y);
		this.zoomExpr = zoomExpr;
		this.nbArcs = complete ? 4 : 1;
		this.functionX = functionX;
		this.functionY = functionY;
	}
	
	public CircularMoveOrder(float x, float y, FloatExpression zoomExpr, boolean complete) {
		this(x, y, zoomExpr, complete, null, null);
	}
	
	public CircularMoveOrder(float x, float y) {
		this(x, y, null, false, null, null);
	}

	@Override
	public Pointf move() {
		double angle = factor * iota;
		mobileElement.vx = functionX(angle);
		mobileElement.vy = functionY(angle);
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
	
	private float functionX(double angle) {
		if (functionX != null) {
			return (float) (radius.x * functionX.evaluate(ctx) * pasX * factor);
		} else {
			return (float) (radius.x * Math.cos(angle) * pasX * factor);
		}
	}

	private float functionY(double angle) {
		if (functionY != null) {
			return (float) (-radius.y * functionY.evaluate(ctx) * pasY * factor);
		} else {
			return (float) (-radius.y * Math.sin(angle) * pasY * factor);
		}
	}

	@Override
	public void init(Mover p_wrapper) {
		super.init(p_wrapper);
		
		EntityType entTyp = mobile.getEntityType();
		if (entTyp.isElement() || entTyp.isPerso() || entTyp.isFont()) {
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
		if (zoomExpr != null || functionX != null || functionY != null) {
			ctx = new SpriteEntityContext(mobileElement) {
				
				@Override
				public float getValue(String key) {
					if ("bell".equals(key)) {
						return (float) Math.abs(Math.cos(Math.PI / nbFrames * iota));
					} else if ("alpha".equals(key)) {
						return (float) (factor * iota);
					}
					return super.getValue(key);
				}

			};
		}
	}
}
