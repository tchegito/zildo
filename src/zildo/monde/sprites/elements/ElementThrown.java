package zildo.monde.sprites.elements;

import zildo.monde.map.Angle;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
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

        if (p_shooter != null) {
        	relativeZ=EngineZildo.mapManagement.getCurrentMap().readAltitude((int) p_shooter.x/16, (int) p_shooter.y/16);
        } else {
        	relativeZ = p_startZ;
        }
        switch (p_angle) {
            case NORD:
                vy = -p_speed;
                nSpr = ElementDescription.ARROW_UP.ordinal();
                break;
            case EST:
                vx = p_speed;
                nSpr = ElementDescription.ARROW_RIGHT.ordinal();
                break;
            case SUD:
                vy = p_speed;
                x += 2;
                y += 4;
                nSpr = ElementDescription.ARROW_DOWN.ordinal();
                break;
            case OUEST:
                vx = -p_speed;
                nSpr = ElementDescription.ARROW_LEFT.ordinal();
                break;
        }
        angle = p_angle;
        setLinkedPerso(p_shooter);
        flying = true;
    }
}
