package junit.area;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import tools.annotations.SoundEnabled;
import zildo.client.sound.BankSound;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Vector2f;
import zildo.server.EngineZildo;

public class ScenesBugFixes extends EngineUT {

	@Test @SoundEnabled
	public void secretSoundInLibrary() {
		EngineZildo.scriptManagement.accomplishQuest("secretDoorRevealed", false);
		mapUtils.loadMap("d4m2");
		waitEndOfScripting();
		// Ruben noticed sound was played each time => that was a bug !
		verify(EngineZildo.soundManagement, never()).playSound(eq(BankSound.ZildoSecret), any(PersoPlayer.class), eq(false));
	}
	
	@Test
	public void nailsInForest() {
		mapUtils.loadMap("sousbois3");
		int nails = 1277;
		// Check that nails are in place
		Assert.assertEquals(nails, mapUtils.area.readmap(11, 28));
		EngineZildo.scriptManagement.accomplishQuest("removeNailForMoonStone", true);
		waitEndOfScripting();
		// After button is presed, check that nails are removed
		Assert.assertEquals(54, mapUtils.area.readmap(11, 28));
		
		// Reload map, then check again
		mapUtils.loadMap("sousbois3");
		waitEndOfScripting();
		Assert.assertEquals("Nails should have been removed !", 54, mapUtils.area.readmap(11, 28));
	}
	
	@Test
	public void blowFarmMountain() {
		mapUtils.loadMap("eleog");
		PersoPlayer zildo = spawnZildo(215, 49);
		waitEndOfScripting();
		
		// Plant dynamite
		zildo.setWeapon(new Item(ItemKind.DYNAMITE));
		zildo.setCountBomb(20);
		zildo.attack();
		Assert.assertEquals(19, zildo.getCountBomb());
		Assert.assertNotNull(findEntityByDesc(ElementDescription.DYNAMITE));
		zildo.setY(zildo.y+30);	// Move hero to escape from dynamite fire
		
		// Wait for dynamite to blow
		renderFrames(120+1);
		Assert.assertNull(findEntityByDesc(ElementDescription.DYNAMITE));
		// Check that related quest has been triggered
		Assert.assertTrue(EngineZildo.scriptManagement.isQuestDone("eleog(13, 3)"));
		
		// Wait for map change (going to "ferme")
		simulateDirection(0, -2);
		while (!EngineZildo.mapManagement.isChangingMap(zildo)) {
			renderFrames(1);
		}
		while (EngineZildo.mapManagement.isChangingMap(zildo)) {
			renderFrames(1);
		}
		waitEndOfScripting();
		Assert.assertEquals("ferme", EngineZildo.mapManagement.getCurrentMap().getName());
		renderFrames(20);
		Assert.assertFalse(EngineZildo.mapManagement.isChangingMap(zildo));
		System.out.println(zildo);
	}
	
	
	/** Check that particular bug found one day is ok now.
	 * Problem was about pathfinder's strategy to move a blocking character. He was trying to reach
	 * a default location, which was impossible. **/
	@Test
	public void sceneUnblockable() {
		// Spawn a character A
		mapUtils.loadMap("eleom1");
		Perso eleo = EngineZildo.persoManagement.getNamedPerso("eleoric");
		eleo.setPos(new Vector2f(101, 102));
		spawnZildo(107, 113);
		waitEndOfScripting();

		EngineZildo.scriptManagement.execute("vactoComeIn", true);
		
		// Wait for the end of scene
		waitEndOfScriptingPassingDialog();
		
		// If we reach this point, we can conclude that script has worked without any blocking character !
	}
	
	@Test
	public void sceneUnblockable2() {
		// Spawn a character A
		mapUtils.loadMap("eleom1");
		Perso eleo = EngineZildo.persoManagement.getNamedPerso("eleoric");
		eleo.setPos(new Vector2f(101, 102));
		PersoPlayer zildo = spawnZildo(123, 108);
		waitEndOfScripting();

		EngineZildo.scriptManagement.execute("vactoComeIn", true);
		
		// Wait for the end of scene
		waitEndOfScriptingPassingDialog();
		
		// If we reach this point, we can conclude that script has worked without any blocking character !
		// Anyway, check that hero can move == attente equals 0
		Assert.assertEquals(0,  zildo.getAttente());
	}
	
	@Test
	public void polakyCaveStucked() {
		EngineZildo.scriptManagement.accomplishQuest("tonneau_polakyg", false);
		mapUtils.loadMap("polakyg");
		waitEndOfScripting();
		
		List<SpriteEntity> barrels = findEntitiesByDesc(ElementDescription.BARREL);
		Assert.assertEquals(5, barrels.size());
		SpriteEntity barrel = barrels.stream().filter(b -> "barrel1".equals(b.getName())).findFirst().orElse(null);
		Assert.assertNotNull(barrel);
		System.out.println(barrel.y);
	}
}
