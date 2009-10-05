package zildo.monde.sprites.elements;

import zildo.client.SoundPlay.BankSound;
import zildo.monde.collision.Collision;
import zildo.monde.collision.DamageType;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.utils.CompositeElement;
import zildo.server.EngineZildo;

public class ElementImpact extends Element {

	enum ImpactKind {
		SIMPLEHIT(ElementDescription.IMPACT1, 4,1), 
		EXPLOSION(ElementDescription.EXPLO1, 3,1), 
		FIRESMOKE(ElementDescription.EXPLOSMOKE1, 3,4);
		
		ElementDescription desc;
		int seqLong;
		int speed;
		
		private ImpactKind(ElementDescription p_desc, int p_seqLong, int p_speed) {
			desc=p_desc;
			seqLong=p_seqLong;
			speed=p_speed;
		}
	}
	
	int counter;
	int startX, startY;
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
			case FIRESMOKE:
				setSprModel(kind.desc);
				break;
			case EXPLOSION:
				setSprModel(ElementDescription.EXPLO1);
				y+=getSprModel().getTaille_y()/2;
				composite=new CompositeElement(this);
				EngineZildo.soundManagement.broadcastSound(BankSound.Explosion, this);
		}
		addSpr=0;
		// Stock the initial location
		startX=p_startX;
		startY=p_startY;
	}
	
	public void animate() {
		counter++;
		switch (kind) {
			case SIMPLEHIT:
			case FIRESMOKE:
				addSpr=counter / kind.speed;
				if (addSpr == kind.seqLong) {
					dying=true;
					visible=false;
				} else {
					setSprModel(kind.desc, addSpr);
				}
				setAjustedX((int) x);
				setAjustedY((int) y+getSprModel().getTaille_y()/2);
				break;
			case EXPLOSION:
				int valCounter=counter;
				if (valCounter == seqExplo.length) {	// End of the sequence
					composite.die(false);
					// Create the ending smoke fog
					EngineZildo.spriteManagement.spawnSprite(new ElementImpact(startX, startY, ImpactKind.FIRESMOKE));
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
				super.animate();
		}
	}
	
    @Override
    public Collision getCollision() {
        if (kind == ImpactKind.EXPLOSION) {
            return composite.getCollision();
        }
        return null;
    }

    public DamageType getDamageType() {
    	return DamageType.EXPLOSION;
    }
    
    @Override
    public boolean isSolid() {
        return kind == ImpactKind.EXPLOSION;
    }      
}
