package zildo.monde.sprites.elements;

import zildo.monde.sprites.desc.ElementDescription;

public class ElementQuadDamage extends ElementGoodies {

	int anim=0;
	
	public ElementQuadDamage(int p_x, int p_y) {
		super();
		x=p_x;
		y=p_y;
		z=3;
		nSpr=ElementDescription.QUAD1.ordinal();
		volatil=false;
		
        // Add a shadow
		addShadow(ElementDescription.SHADOW_SMALL);
	}
	
	public void animate() {
		super.animate();
		
		anim++;
		addSpr= (anim / 8) % 8;
		
	}
}
