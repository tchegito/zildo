package zildo.monde.sprites.utils;

import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZildoDescription;

public class Sprite {

	public SpriteDescription spr;	// Identify the sprite (Bank + sprite number)
	public int reverse;				// Reverse (horizontal and/or vertical)
	
	public Sprite(int p_nSpr, int p_nBank, int p_reverse) {
		switch (p_nBank) {
		case SpriteBank.BANK_ELEMENTS:
			spr=ElementDescription.fromInt(p_nSpr);
			break;
		case SpriteBank.BANK_ZILDO:
			spr=ZildoDescription.fromInt(p_nSpr);
			break;
		}
		reverse=p_reverse;
	}
}
