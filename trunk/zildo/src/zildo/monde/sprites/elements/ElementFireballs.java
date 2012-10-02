package zildo.monde.sprites.elements;

import zildo.client.sound.BankSound;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

public class ElementFireballs extends ElementChained {

	public ElementFireballs(int x, int y, Angle angle) {
		super(x, y);
		this.angle = angle;
		delay = 120;
	}
	
	@Override
	protected Element createOne(int p_x, int p_y) {
		Element fireball = new ElementFireball(angle, p_x, p_y, 4, 2f, null); 
		EngineZildo.soundManagement.broadcastSound(BankSound.CannonBall, this);
		return fireball;
	}
	
	public class ElementFireball extends ElementThrown {

		int count;
		
	    public ElementFireball(Angle p_angle, int p_startX, int p_startY,
		    int p_startZ, float p_speed, Perso p_shooter) {
		super(p_angle, p_startX, p_startY, p_startZ, p_speed, p_shooter);
	        setDesc(ElementDescription.BIG_FIRE_BALL);
	        //setSpecialEffect(EngineFX.QUAD);
	    }
	    
	    @Override
	    public void animate() {
	    	count++;
	    	if (count % 4 == 0) {
	    		rotation = Rotation.fromInt((rotation.value + 1) % 4);
	    	}
	        super.animate();
	    }
	}
}
