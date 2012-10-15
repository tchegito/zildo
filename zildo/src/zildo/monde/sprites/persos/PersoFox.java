package zildo.monde.sprites.persos;

import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.ElementGuardWeapon;
import zildo.monde.sprites.elements.ElementGuardWeapon.GuardWeapon;

public class PersoFox extends PersoShadowed {

	ElementGuardWeapon guardWeapon;
	
	public PersoFox() {
		super(ElementDescription.SHADOW, 3);
		pathFinder.speed = 0.5f;
		pv = 2;
		
		weapon = new Item(ItemKind.BOW);
		guardWeapon = new ElementGuardWeapon(this);
		guardWeapon.setWeapon(GuardWeapon.BOW);
		addPersoSprites(guardWeapon);
		setEn_bras(guardWeapon);
		
	}
}
