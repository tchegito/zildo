package junit.script;

import org.junit.Test;

import org.junit.Assert;
import org.junit.Before;

import tools.EngineScriptUT;
import zildo.fwk.script.context.SceneContext;
import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.sprites.desc.PersoDescription;
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
		scriptMgmt.execute("testVisibility", true, new SceneContext(), null);

		int expectedPhase = 1;
		for (int i=0;i<2;i++) {
			synchroVariable("phase", expectedPhase++);
			Perso perso = EngineZildo.persoManagement.getNamedPerso("man");
			Assert.assertFalse("Perso should be invisible !", perso.isVisible());
			synchroVariable("phase", expectedPhase++);
			Assert.assertTrue("Perso should be visible !", perso.isVisible());
		}
	}
	
	// Test twice a visible/invisible process on character during a persoAction
	@Test
	public void visibilityPersoAction() {
		waitEndOfScripting();
		scriptMgmt.execute("launchPersoTest", true, new SceneContext(), null);
		
		int expectedPhase = 1;
		for (int i=0;i<2;i++) {
			synchroVariable("phase", expectedPhase++);
			Perso perso = EngineZildo.persoManagement.getNamedPerso("ben");
			Assert.assertFalse("Perso should be invisible !", perso.isVisible());
			synchroVariable("phase", expectedPhase++);
			Assert.assertTrue("Perso should be visible !", perso.isVisible());
		}
	}
}
