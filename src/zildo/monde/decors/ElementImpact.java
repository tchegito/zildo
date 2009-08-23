package zildo.monde.decors;

import java.util.List;

public class ElementImpact extends Element {

	int count;
	
	public ElementImpact(int p_startX, int p_startY) {
		x=p_startX;
		y=p_startY;
		z=4;
		count=0;
		setSprModel(ElementDescription.IMPACT2);
		addSpr=0;
	}
	
	public List<SpriteEntity> animate() {
		count++;
		addSpr=count;
		if (addSpr == 4) {
			dying=true;
			visible=false;
		} else {
			setSprModel(ElementDescription.IMPACT1, addSpr);
		}
		setAjustedX((int) x);
		setAjustedY((int) y+getSprModel().getTaille_y()/2);
		return null;
	}
}
