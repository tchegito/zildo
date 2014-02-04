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

package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/**
 * Abstract class which modelizes a thrown element (arrow, boomerang, ...). It needs a start angle/position, and the thrower.
 *
 * By default, the element's trajectory doesn't consider the Z coordinate.
 *
 * @author tchegito
 */
public abstract class ElementThrown extends Element {

	/**
	 * 
	 * @param p_angle
	 * @param p_startX
	 * @param p_startY
	 * @param p_startZ Used only if p_shooter is NULL, otherwise we get the Z from the shooter
	 * @param p_speed
	 * @param p_shooter Character who throw this element
	 */
    public ElementThrown(Angle p_angle, int p_startX, int p_startY, int p_startZ, float p_speed, Perso p_shooter) {
        x = p_startX;
        y = p_startY;
        z = p_startZ;
        
        if (p_shooter != null) {
        	relativeZ=EngineZildo.getMapManagement().getCurrentMap().readAltitude((int) p_shooter.x/16, (int) p_shooter.y/16);
        } else {
        	relativeZ = p_startZ;
        }
        Point dir=p_angle.coords;
        vx=p_speed*dir.x;
        vy=p_speed*dir.y;
        switch (p_angle) {
            case NORD: case NORDEST: case NORDOUEST:
                nSpr = ElementDescription.ARROW_UP.ordinal();
                break;
            case EST:
                nSpr = ElementDescription.ARROW_RIGHT.ordinal();
                break;
            case SUD: case SUDEST: case SUDOUEST:
                x += 2;
                y += 4;
                nSpr = ElementDescription.ARROW_DOWN.ordinal();
                break;
            case OUEST:
                nSpr = ElementDescription.ARROW_LEFT.ordinal();
                break;
        }
        if (p_angle.isDiagonal()) {
            float coeff = Constantes.cosPiSur4;
        	vx = coeff * vx;
        	vy = coeff * vy;
        }
        angle = p_angle;
        setLinkedPerso(p_shooter);
        flying = true;
    }
}
