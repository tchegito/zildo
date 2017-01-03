package zildo.monde.sprites.elements;

import zildo.fwk.ZUtils;
import zildo.monde.sprites.desc.ElementDescription;

public class ElementHearts extends ElementChained {

	int nHearts = 0;

	public ElementHearts(int p_x, int p_y) {
		super(p_x, p_y);
	}

	@Override
	protected Element createOne(int p_x, int p_y) {
		int px = p_x + ZUtils.randomRange(2);
		int py = p_y + ZUtils.randomRange(2);
		delay = 12 + (int) Math.random() * 10;

		nHearts++;
		if (nHearts == 3) { // Stop the animation about 8 sprites
			dying = true;
		}
		Element heart = new Element() {
			@Override
			public void animate() {
				alpha -= 2;
				super.animate();
				dying = alpha == 0;
			}
		};
		heart.setDesc(ElementDescription.HEART);
		heart.setX(px);
		heart.setY(py);
		heart.setZ(8);
		heart.az = 0.011f;
		heart.vx = 0.2f;
		heart.ax = -0.003f;
		return heart;
	}

}
