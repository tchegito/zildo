package junit.script;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tools.EngineScriptUT;
import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

public class CheckSceneProcess extends EngineScriptUT {

	@Before
	public void init() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/scenes"));
	}
	
	// Test twice a visible/invisible process on character during a scene
	@Test
	public void visibility() {
		waitEndOfScripting();
		executeScene("testVisibility");

		checkVisibilityTwice("man");
	}
	
	// Test twice a visible/invisible process on character during a persoAction
	@Test
	public void visibilityPersoAction() {
		waitEndOfScripting();
		executeScene("launchPersoTest");
		
		checkVisibilityTwice("ben");
	}
	
	private void checkVisibilityTwice(String name) {
		
		int expectedPhase = 1;
		for (int i=0;i<2;i++) {
			synchroVariable("phase", expectedPhase++);
			Perso perso = EngineZildo.persoManagement.getNamedPerso(name);
			Assert.assertFalse("Perso should be invisible !", perso.isVisible());
			synchroVariable("phase", expectedPhase++);
			Assert.assertTrue("Perso should be visible !", perso.isVisible());
		}
	}
}
