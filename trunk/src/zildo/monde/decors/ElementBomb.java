package zildo.monde.decors;

import java.util.List;

import zildo.fwk.gfx.PixelShaders;
import zildo.monde.decors.ElementImpact.ImpactKind;
import zildo.server.EngineZildo;

public class ElementBomb extends Element {

	int counter;
	
	public ElementBomb(int p_startX, int p_startY, int p_startZ) {
		x=p_startX;
		y=p_startY;
		z=p_startZ;
		setSprModel(ElementDescription.BOMB);
		counter=100;
	}
	
	public List<SpriteEntity> animate() {
		counter--;
		if (counter==0) {
			dying=true;
			EngineZildo.spriteManagement.spawnSprite(new ElementImpact((int) x, (int) y, ImpactKind.EXPLOSION));
		} else if (counter<30) {
			setSpecialEffect(PixelShaders.ENGINEFX_PERSO_HURT);
		}
		return super.animate();
	}
}
