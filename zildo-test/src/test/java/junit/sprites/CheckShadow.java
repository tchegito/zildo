package junit.sprites;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementZildo;

public class CheckShadow extends EngineUT {

	@Test
	public void musicNotesShouldntHave() {
		mapUtils.loadMap("sousbois7");
		PersoPlayer hero = spawnZildo(273,311);
		waitEndOfScripting();
		Item flut = new Item(ItemKind.FLUT);
		hero.getInventory().add(flut);
		hero.setWeapon(flut);
		simulatePressButton(Keys.W, 2);
		renderFrames(10);
		Assert.assertEquals(MouvementZildo.PLAYING_FLUT, hero.getMouvement());
		Element note = (Element) waitForSpecificEntity(ElementDescription.NOTE, ElementDescription.NOTE2);
		Assert.assertFalse(note.hasShadow());
		System.out.println(hero);
	}
}
