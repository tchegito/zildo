package junit.input;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.resource.KeysConfiguration;

public class TestUnexpectedKeys extends EngineUT {

	// Issue 114
	@Test
	public void stuckInInventory() {
		waitEndOfScripting();
		
		mapUtils.loadMap("cavef5");
		PersoPlayer hero = spawnZildo(255, 116);
		Item item = new Item(ItemKind.FIRE_RING);
		hero.getInventory().add(item);
		hero.setWeapon(item);
		
		while (hero.isInventoring()) {
			simulatePressButton(KeysConfiguration.PLAYERKEY_INVENTORY.code, 1);
		}
		simulatePressButton(KeysConfiguration.PLAYERKEY_INVENTORY.code, 1);
		Assert.assertFalse(hero.isInventoring());
	}
}
