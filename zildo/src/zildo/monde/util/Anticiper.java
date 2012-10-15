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

package zildo.monde.util;

import zildo.monde.sprites.persos.Perso;

/**
 * @author Tchegito
 *
 */
public class Anticiper {

	float attackSpeed;
	
	public Anticiper(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	
	/**
	 * Calculate the location where attacker and victim might collide (if victim keeps the same move).
	 * @param attacker
	 * @param victim
	 * @return Point
	 */
	public Point anticipeTarget(Perso attacker, Perso victim) {
		float locX = victim.x;
		float locY = victim.y;
		// Is victim moving ?
		double nbFrames = 1;
		if (victim.deltaMoveX != 0f || victim.deltaMoveY != 0f) {
			double Dx = victim.x - attacker.x;
			double Dy = victim.y - attacker.y;
			// Calculate the coefficient of the second degree equation
			double a = square(victim.deltaMoveX) + square(victim.deltaMoveY) - square(attackSpeed);
			double b = 2 * (Dx * victim.deltaMoveX + Dy * victim.deltaMoveY);
			double c = square(Dx) + square(Dy);
			if (a == 0) {	// Special case (avoid divide by zero)
				nbFrames = Math.abs(-c / b);
			} else {
				// Calculate discriminant
				double discrimant = square(b) - 4 * a * c;
				double root = Math.sqrt(discrimant);
				double first = (-b - root) / (2 * a);
				double second = (-b + root) / (2 * a);
				nbFrames = Math.min(Math.abs(first), Math.abs(second));
			}
			locX = (float) (victim.x + victim.deltaMoveX * nbFrames);
			locY = (float) (victim.y + victim.deltaMoveY * nbFrames);
			if (Point.distance(locX, locY, victim.x, victim.y) > 100) {
				// Is is unrealistic ? Then target victim location
				locX = victim.x;
				locY = victim.y;
			}
		}
		
		return new Point(locX, locY);
	}
	
	private double square(double x) {
		return Math.pow(x, 2);
	}
}
