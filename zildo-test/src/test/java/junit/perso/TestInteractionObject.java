package junit.perso;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementFire;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.monde.util.Point;
import zildo.monde.util.Vector2f;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

public class TestInteractionObject extends EngineUT {

	PersoPlayer zildo;
	
	@Before
	public void init() {
		zildo = spawnZildo(160, 100);
		// Very important: wait, because automatic behavior doesn't run without
		waitEndOfScripting();
	}
	/** Check when hero takes items that:
	 * -automatic dialog are well triggered
	 * -item are added into his inventory (if possible) **/
	@Test
	public void automaticBehavior() {
		for (ItemKind kind : ItemKind.values()) {
			
			SpriteDescription desc = kind.representation;
			pick(desc);
			// If automatic sentence is planned, check it
			if (kind.getFoundSentence("") != null) {
				Assert.assertNotNull(dialog((ElementDescription) desc));
			}
			// Wait for end of dialog
			waitEndOfScriptingPassingDialog();
			if (kind.canBeInInventory()) {
				Assert.assertTrue(kind+" should have been in hero's inventory ! But he only has "+zildo.getInventory(), zildo.hasItem(kind));
			}
			// Remove item in hero's arms
			zildo.setEn_bras(null);
			EngineZildo.dialogManagement.resetQueue();
		}
	}

	@Test
	public void takeTwiceAutomaticBehavior() {
		// First time
		pick(ElementDescription.KEY);
		Assert.assertNotNull(dialog(ElementDescription.KEY));

		// Second time
		EngineZildo.dialogManagement.resetQueue();
		pick(ElementDescription.KEY);
		Assert.assertEquals(0, EngineZildo.dialogManagement.getQueue().size());
	}
	
	@Test
	public void takeFourMoonFragments() {
		zildo.getInventory().add(new Item(ItemKind.NECKLACE));

		for (int i=0;i<4;i++) {
			pick(ElementDescription.HEART_FRAGMENT);
			Assert.assertNotNull(dialog(ElementDescription.HEART_FRAGMENT));
			EngineZildo.dialogManagement.resetQueue();
		}
	}
	
	/** Hero picks an item and return FALSE if he can get it into his inventory **/
	private boolean pick(SpriteDescription desc) {
		ElementGoodies goodie = new ElementGoodies();
		goodie.setDesc(desc);
		return zildo.pickGoodies(goodie, 0);
	}
	
	/** Get the pronounced sentence. Parameter is just for displaying error. **/
	private String dialog(ElementDescription desc) {
		List<WaitingDialog> wds = EngineZildo.dialogManagement.getQueue();
		Assert.assertTrue("No automatic dialog was found for "+desc, wds.size() > 0);
		Assert.assertEquals("Only one sentence should have been launched !", 1, wds.size());
		String sentence = wds.get(0).sentence;
		System.out.println(sentence);
		return sentence; 
	}
	
	@Test
	public void plantDynamite() {
		// Hero east of the boulder
		plantAndCheck(new Point(160, 100), boulder -> boulder.vx > 0, "East");
		// Hero west of the boulder
		plantAndCheck(new Point(190, 100), boulder -> boulder.vx < 0, "West");
		// Hero south of the boulder
		plantAndCheck(new Point(160, 110), boulder -> boulder.vy < 0, "South");
		// Hero north of the boulder
		plantAndCheck(new Point(160, 90), boulder -> boulder.vy > 0, "North");
	}
	
	private void plantAndCheck(Point heroPos, Predicate<Element> predicate, String message) {
		// Spawn hero and a boulder
		Element boulder = EngineZildo.spriteManagement.spawnElement(ElementDescription.STONE_HEAVY, 170, 100, 0, Reverse.NOTHING, Rotation.NOTHING);
		// Blow the wall
		Element dynamite = plantDynamite(heroPos);
		Assert.assertEquals(3,  zildo.getCountBomb());	// Be sure dynamite is planted
		Assert.assertNotNull(dynamite);

		// Wait for dynamite to explode
		while (true) {
			renderFrames(1);
			if (findEntityByDesc(ElementDescription.DYNAMITE) == null && findEntityByDesc(ElementDescription.DYNAMITE2) == null) {
				break;
			}
		}
		// Check that boulder is projected to the right side
		Assert.assertTrue(boulder.flying);
		Assert.assertTrue("Boulder should have been projected on the "+message, predicate.test(boulder));
	}
	
	private Element plantDynamite(Point heroPos) {
		zildo.setPos(new Vector2f(heroPos.x, heroPos.y));
		zildo.setWeapon(new Item(ItemKind.DYNAMITE));
		zildo.setCountBomb(4);
		zildo.attack();
		return (Element) findEntityByDesc(ElementDescription.DYNAMITE);
	}
	
	@Test
	public void plantAntThrow() {
		waitEndOfScripting();
		zildo = spawnZildo(160, 100);
		
		Element dynamite = plantDynamite(new Point(zildo.x, zildo.y));
		renderFrames(98);
		// Pick and throw it
		zildo.pickItem(ItemKind.DYNAMITE, dynamite);
		renderFrames(5);
		Assert.assertEquals(dynamite, zildo.getEn_bras());
		zildo.throwSomething();
		while (!dynamite.dying) {
			renderFrames(1);
		}
		// Ensure that dynamite is still in the air
		Assert.assertTrue(dynamite.z > 8);
		SpriteEntity entity = findEntityByDesc(ElementDescription.EXPLO1);
		Assert.assertNotNull(entity);
		Assert.assertTrue(Math.abs(dynamite.z - entity.z) < 2);
	}
	
	@Test
	public void takeFork() {
		mapUtils.loadMap("ferme");
		PersoPlayer hero = spawnZildo(858, 668);
		hero.setAngle(Angle.OUEST);
		waitEndOfScripting();
		EngineZildo.scriptManagement.putVarValue("allowedTakeFork", "yes");
		
		// Take the fork
		Assert.assertEquals(0, hero.getInventory().size());
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 1);
		Assert.assertEquals("Hero should have an item now in his inventory !", 1, hero.getInventory().size());
		Item item = hero.getInventory().get(0);
		Assert.assertEquals(ItemKind.SPADE, item.kind);
	}
	
	@Test	// Hero is not allowed to take the fork named 'forbidden' in ferme (only the one from Charles)
	public void takeFork2() {
		mapUtils.loadMap("ferme");
		PersoPlayer hero = spawnZildo(197,747);
		hero.setAngle(Angle.OUEST);
		waitEndOfScripting();
		EngineZildo.scriptManagement.putVarValue("allowedTakeFork", "yes");
		
		// Take the fork
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 1);
		Assert.assertEquals("Hero should not take that forbidden item !", 0, hero.getInventory().size());
	}
	
	@Test
	public void burnCreepers() {
		mapUtils.loadMap("nature1");
		PersoPlayer hero = spawnZildo(152, 122);
		hero.setWeapon(new Item(ItemKind.SPADE));
		hero.setAngle(Angle.EST);
		waitEndOfScripting();
		
		// 1) Grab leaves with his fork
		simulatePressButton(KeysConfiguration.PLAYERKEY_ATTACK.code, 1);
		Assert.assertFalse(hero.canFork());
		
		// 2) Walk to the flames in the alcove
		simulateDirection(0, -1);
		renderFrames(5);
		while (hero.deltaMoveY != 0) {
			renderFrames(5);
		}
		Element leaves = (Element) findEntityByDesc(ElementDescription.BUNCH_LEAVESFORK);
		Assert.assertNotNull(leaves); 
		Assert.assertEquals(1,  getBurningElements().size());
		
		// 3) Walk to a creeper to ignite it
		simulateDirection(new Vector2f(-0.7f, 0.7f));
		renderFrames(30);
		simulateDirection(-1, -1);
		renderFrames(35);
		simulateDirection(0, 0);
		Assert.assertEquals(3,  getBurningElements().size());
		
		// 4) Wait for elements to finish burning
		renderFrames(Element.MAX_TIME_BURNING);
		Assert.assertEquals(0,  getBurningElements().size());
		Assert.assertTrue(hero.canFork());
	}
	
	private List<ElementFire> getBurningElements() {
		List<ElementFire> burnings = ZUtils.arrayList();
		for (SpriteEntity e : EngineZildo.spriteManagement.getSpriteEntities(null)) {
			if (e instanceof ElementFire) {
				burnings.add( (ElementFire) e);
			}
		}
		return burnings;
	}
}
