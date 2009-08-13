package zildo.monde.decors;

import zildo.monde.map.Angle;
import zildo.monde.persos.Perso;

public class ElementArrow extends Element {

	final static float arrowSpeed=2.0f;
	
	public ElementArrow(Angle p_angle, int p_startX, int p_startY, Perso p_shooter) {
		x=p_startX;
		y=p_startY;
		switch (p_angle) {
		case NORD:
			vy=-arrowSpeed;
			nSpr=ElementDescription.ARROW_UP.ordinal();
			break;
		case EST:
			vx=arrowSpeed;
			nSpr=ElementDescription.ARROW_RIGHT.ordinal();
			break;
		case SUD:
			vy=arrowSpeed;
			x+=2;
			y+=4;
			nSpr=ElementDescription.ARROW_DOWN.ordinal();
			break;
		case OUEST:
			vx=-arrowSpeed;
			nSpr=ElementDescription.ARROW_LEFT.ordinal();
			break;
		}

		z=6;
		angle=p_angle;
		linkedPerso=p_shooter;
	}
}
