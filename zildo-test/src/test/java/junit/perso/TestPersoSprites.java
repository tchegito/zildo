package junit.perso;

import org.junit.Test;

import org.junit.Assert;

import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.desc.SpriteDescription;

public class TestPersoSprites {

	@Test
	public void transitionBetweenBanks() {
		for (PersoDescription desc : PersoDescription.values()) {
			if (desc != PersoDescription.FIREFLY &&
				desc != PersoDescription.ZILDO) {	// Exception for this one
				int nBank = desc.getBank();
				int n = desc.first();
				SpriteDescription spriteDesc = SpriteDescription.Locator.findSpr(nBank, n);
				// Check for bank and sprite index
				String bankName = SpriteStore.sprBankName[nBank];
				Assert.assertEquals("Bank and n-sprite should have been the same for "+desc+"\nFound "+spriteDesc+" nSprite="+n+" nBank="+bankName+" indexSprite="+n, desc, spriteDesc);
			}
		}
	}
}
