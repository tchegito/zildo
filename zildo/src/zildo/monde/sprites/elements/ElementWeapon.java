package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.ElementDescription;

public class ElementWeapon extends ElementGoodies {

	public ElementWeapon(int p_x, int p_y) {
		super();
		x=p_x;
		y=p_y;
		volatil=false;
	}
	
	@Override
	public boolean isGoodies() {
		if (desc != ElementDescription.SPADE) {
			return super.isGoodies();
		}
		return false;
	}
}
