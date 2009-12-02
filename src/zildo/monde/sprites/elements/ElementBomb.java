package zildo.monde.sprites.elements;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.gfx.PixelShaders.EngineFX;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class ElementBomb extends Element {

	Perso shooter;
	int counter;
	
	public ElementBomb(int p_startX, int p_startY, int p_startZ, Perso p_shooter) {
		x=p_startX;
		y=p_startY;
		z=p_startZ;
		setSprModel(ElementDescription.BOMB);
		counter=100;
		
        // Add a shadow
		addShadow(ElementDescription.SHADOW_SMALL);
        
        EngineZildo.soundManagement.broadcastSound(BankSound.PlanteBombe, this);
	}
	
	public void animate() {
		counter--;
		if (counter==0) {
			dying=true;
			shadow.dying=true;
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.EXPLOSION, shooter));
		} else if (counter<30) {
			setSpecialEffect(EngineFX.PERSO_HURT);
		}
		super.animate();
	}
	
    public Collision getCollision() {
        return null;
    }

}
