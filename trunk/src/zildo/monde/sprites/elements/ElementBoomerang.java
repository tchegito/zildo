package zildo.monde.sprites.elements;

import zildo.client.SoundPlay.BankSound;
import zildo.monde.map.Angle;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * Zildo's boomerang
 * 
 * He throws it, then it came back. It has some miscellaneous properties :
 * -when it hits something, came back.
 * -when it cames back, it can't collide any tile, but it can wound enemies. (see {@link #isSolid()}
 * 
 * @author tchegito
 *
 */
public class ElementBoomerang extends ElementThrown {

	private static final float speed=2.5f;
	
	int count=0;
	boolean comingBack=false;
	
    public ElementBoomerang(Angle p_angle, int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
        super(p_angle, p_startX, p_startY, p_startZ, speed, p_shooter);
        setSprModel(ElementDescription.BOOMERANG1);
        ax=-vx*0.015f;
        ay=-vy*0.015f;
	}
    
    public void animate() {
    	addSpr=(count/5) % 4;
    	if (count % 9 == 0) {
    		EngineZildo.soundManagement.broadcastSound(BankSound.Boomerang, this);
    	}
    	count+=1;
    	if (comingBack) {
    		// Boomerang is coming back to Zildo
    		Perso p=(Perso) getLinkedPerso();
    		float deltaY=y-p.y;
    		float deltaX=x-p.x;
    		if (Math.abs(deltaX)<=speed && Math.abs(deltaY)<=speed) {
    			// Zildo got it back
    			dying=true;
    		}
    		// Calculate hypothenus between boomerang and zildo's location to get correct speed
    		double hypo=Math.sqrt(deltaY*deltaY + deltaX*deltaX);
    		float speedHypo=(float) (speed/hypo);
    		vx=-speedHypo*deltaX;
    		vy=-speedHypo*deltaY;
    	} else if (Math.abs(vx)<=0.1f && Math.abs(vy)<=0.1f) {
    		comingBack=true;
    	}
    	super.animate();
    }

    public boolean beingCollided() {
    	// Boomerang hit something, so give him back to Zildo
    	comingBack=true;
		EngineZildo.soundManagement.broadcastSound(BankSound.BoomerangTape, this);
		EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.SIMPLEHIT, null));
    	return true;
    }
    
    public boolean isSolid() {
		return !comingBack;
	}
}
