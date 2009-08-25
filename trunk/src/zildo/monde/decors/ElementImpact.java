package zildo.monde.decors;

import java.util.List;

public class ElementImpact extends Element {

	enum ImpactKind {
		SIMPLEHIT, EXPLOSION;
	}
	
	int counter;
	ImpactKind kind;

	CompositeElement composite;
	
	private final static int[] seqExplo={0,0,0,1,1,2,2,1,1,2,2,3};
	
	public ElementImpact(int p_startX, int p_startY, ImpactKind p_kind) {
		x=p_startX;
		y=p_startY;
		z=4;
		counter=0;
		kind=p_kind;
		switch (p_kind) {
			case SIMPLEHIT:
				setSprModel(ElementDescription.IMPACT1);
				break;
			case EXPLOSION:
				setSprModel(ElementDescription.EXPLO1);
				y+=getSprModel().getTaille_y()/2;
				composite=new CompositeElement(this);
		}
		addSpr=0;
	}
	
	public List<SpriteEntity> animate() {
		counter++;
		switch (kind) {
			case SIMPLEHIT:
				addSpr=counter;
				if (addSpr == 4) {
					dying=true;
					visible=false;
				} else {
					setSprModel(ElementDescription.IMPACT1, addSpr);
				}
				setAjustedX((int) x);
				setAjustedY((int) y+getSprModel().getTaille_y()/2);
				break;
			case EXPLOSION:
				int valCounter=counter;
				if (valCounter == seqExplo.length) {
					composite.die(false);
				} else {
					addSpr=seqExplo[valCounter];
					if (addSpr == 1) {
						// Create 3 other elements to create the entire explosion
						composite.squareShape(0,0);
					} else if (addSpr == 3) {
						composite.die(true);
						composite.squareShape(4,4);
						addSpr=2;
					}
					if (addSpr < 3) {
						composite.setSprModel(ElementDescription.EXPLO1, addSpr);
					}
				}
		}
		return null;
	}
}
