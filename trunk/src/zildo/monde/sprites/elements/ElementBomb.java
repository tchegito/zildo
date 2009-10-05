package zildo.monde.sprites.elements;

import zildo.client.SoundPlay.BankSound;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.collision.Collision;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementImpact.ImpactKind;
import zildo.server.EngineZildo;

public class ElementBomb extends Element {

	int counter;
	Element shadow;
	
	public ElementBomb(int p_startX, int p_startY, int p_startZ) {
		x=p_startX;
		y=p_startY;
		z=p_startZ;
		setSprModel(ElementDescription.BOMB);
		counter=100;
		
        // Add a shadow
        shadow = new Element();
        shadow.x = x;
        shadow.y = y;
        shadow.z = -1;
        shadow.nBank = SpriteBank.BANK_ELEMENTS;
        shadow.nSpr = ElementDescription.SHADOW_SMALL.ordinal();
        shadow.setSprModel(ElementDescription.SHADOW_SMALL);
        EngineZildo.spriteManagement.spawnSprite(shadow);
        
        EngineZildo.soundManagement.broadcastSound(BankSound.PlanteBombe, this);
	}
	
	public void animate() {
		counter--;
		if (counter==0) {
			dying=true;
			shadow.dying=true;
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.EXPLOSION));
		} else if (counter<30) {
			setSpecialEffect(PixelShaders.ENGINEFX_PERSO_HURT);
		}
		super.animate();
	}
	
    public Collision getCollision() {
        return null;
    }

}
