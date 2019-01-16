package junit.script;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineScriptUT;
import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Angle;
import zildo.server.EngineZildo;

/**
 * Test class which ensures that script cursor is going forward following the rules.
 * 
 * @author Tchegito
 *
 */
public class CheckScriptCursor extends EngineScriptUT {
		
	// Check that script's cursor moves forward when reaching some 'state' actions
	@Test
	public void checkAutoForward() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/forward"));
		waitEndOfScripting();
		scriptMgmt.execute("caller",  false);
		renderFrames(1);
		// 1) Four variables should be set in one single frame
		Assert.assertEquals(scriptMgmt.getVarValue("stateA"), "1.0");
		Assert.assertEquals(scriptMgmt.getVarValue("stateB"), "2.0");
		Assert.assertEquals(scriptMgmt.getVarValue("stateC"), "3.0");
		Assert.assertEquals(scriptMgmt.getVarValue("stateD"), "4.0");
		
		// 2) 'if' clause should be started in the same frame
		Assert.assertEquals(scriptMgmt.getVarValue("stateE"), null);
		renderFrames(1);
		Assert.assertEquals(scriptMgmt.getVarValue("stateE"), "5.0");
		
		// 3) 'loop' clause too
		renderFrames(1);
		Assert.assertEquals(scriptMgmt.getVarValue("stateF"), "6.0");

		// 4) 'for' clause too, even nested in 'loop'
		renderFrames(1);
		Assert.assertEquals(scriptMgmt.getVarValue("stateG"), "7.0");
	}
	
	@Test
	public void nestedAndActions() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/forward"));
		waitEndOfScripting();
		scriptMgmt.execute("moreComplicated",  false);
		
		PersoPlayer zildo = EngineZildo.persoManagement.getZildo();
		
		renderFrames(2);
		Assert.assertEquals(Angle.SUD, zildo.getAngle());
	}
	
	@Test
	public void forAndTile() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/forward"));
		waitEndOfScripting();
		scriptMgmt.execute("forAndTile",  false);
		renderFrames(1);
		Assert.assertEquals("3.0", scriptMgmt.getVarValue("j"));
	}
	
	@Test
	public void doubleForAndTile() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/forward"));
		waitEndOfScripting();
		scriptMgmt.execute("doubleForAndTile",  false);
		renderFrames(1);
		Assert.assertEquals("3.0", scriptMgmt.getVarValue("j"));
		Assert.assertEquals("6.0", scriptMgmt.getVarValue("i"));
	}
	
	@Test
	public void blockingFor() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/forward"));
		waitEndOfScripting();
		scriptMgmt.execute("blockingFor",  true);
		for (int i=0;i<10;i++) {
			renderFrames(1);
			Assert.assertTrue(scriptMgmt.isScripting());
		}
		renderFrames(1);	// Close the 'for' loop
		Assert.assertFalse(scriptMgmt.isScripting());
	}
	
	
}