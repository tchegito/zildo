package junit.scenario;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineUT;
import zildo.fwk.net.www.WorldRegister;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.monde.util.Angle;
import zildo.resource.KeysConfiguration;
import zildo.server.EngineZildo;

import static org.mockito.Mockito.*;

public class CheckEpisode4 extends EngineUT {

	PersoPlayer zildo;
	Perso charles;

	@Test
	public void checkForkTakable() {
		EngineZildo.scriptManagement.accomplishQuest("fermierperdu", false);
		mapUtils.loadMap("ferme");
		charles = persoUtils.persoByName("charles");

		charles.setQuel_deplacement(MouvementPerso.IMMOBILE, false);
		zildo = spawnZildo(847, 675);	// Spawn hero right below the fork
		zildo.setAngle(Angle.NORD);
		Assert.assertEquals(0, zildo.getInventory().size());
		waitEndOfScripting();


		Assert.assertEquals("no", EngineZildo.scriptManagement.getVarValue("allowedTakeFork"));

		// Assume that fork is untakable
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
		renderFrames(5);
		Assert.assertEquals(0, zildo.getInventory().size());

		talkToCharles();
		Assert.assertEquals("no", EngineZildo.scriptManagement.getVarValue("allowedTakeFork"));

		// Now trigger the quest enabling Charles to talk about the fork
		EngineZildo.scriptManagement.accomplishQuest("trig_falcor", false);
		
		talkToCharles();
		waitEndOfScripting();

		Assert.assertEquals("yes", EngineZildo.scriptManagement.getVarValue("allowedTakeFork"));
		
		// Now take the fork
		Assert.assertEquals(0, zildo.getInventory().size());
		zildo.x = 847;
		zildo.y = 675;
		zildo.walkTile(false);
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
		Assert.assertEquals(1, zildo.getInventory().size());
	}
	
	@Test	// remove this test when episode is ready
	public void episode4NotAvailableYet() {
		EngineZildo.worldRegister = mock(WorldRegister.class);
		spawnZildo(160, 100);
		waitEndOfScripting();
		EngineZildo.scriptManagement.execute("ep3_closure", true);
		waitEndOfScriptingPassingDialog();
		Assert.assertNotEquals("voleurs", EngineZildo.mapManagement.getCurrentMap().getName());
	}
	
	// Talk to Charles and wait for the dialog to be over
	private void talkToCharles() {
		zildo.x = charles.x;
		zildo.y = charles.y + 6;
		zildo.walkTile(false);
		renderFrames(1);
		Assert.assertNull(zildo.getDialoguingWith());
		simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
		Assert.assertEquals(charles, zildo.getDialoguingWith());
		while (zildo.getDialoguingWith() != null) {
			simulatePressButton(KeysConfiguration.PLAYERKEY_ACTION.code, 2);
		}
	}
}
