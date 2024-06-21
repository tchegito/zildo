package junit.script;

import org.junit.Assert;
import org.junit.Test;

import tools.EngineScriptUT;
import zildo.fwk.script.model.point.PointEvaluator;
import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.util.Point;
import zildo.monde.util.Pointf;

public class CheckBuiltinScript extends EngineScriptUT {
	
	@Test
	public void angle() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/builtin"));
		waitEndOfScripting();
		
		executeScene("testAngle");
		waitEndOfScripting();
		
		Assert.assertEquals("0.0", scriptMgmt.getVariables().get("alpha"));	// Angle = 0
		Assert.assertEquals("1.5707964", scriptMgmt.getVariables().get("beta"));	// Angle = pi/2
		String expectedLocation = "" + PointEvaluator.toSingleFloat(new Point(200, 114));
		Assert.assertEquals(expectedLocation, scriptMgmt.getVariables().get("freeLoc"));	// Angle = pi/2
		Assert.assertEquals(expectedLocation, scriptMgmt.getVariables().get("freeLoc2"));	// Angle = pi/2
		
		// Same with half of the distance
		expectedLocation = "" + PointEvaluator.toSingleFloat(new Point(200, 82));
		Assert.assertEquals(expectedLocation, scriptMgmt.getVariables().get("freeLoc3"));	// Angle = pi/2
				
		Perso bandit = persoUtils.persoByName("bandit");
		assertLocation(bandit, new Pointf(200, 114), true);
	}
	
	@Test
	public void project() {
		scriptMgmt.getAdventure().merge(ScriptReader.loadScript("junit/script/builtin"));
		waitEndOfScripting();
		
		executeScene("testProject");
		waitEndOfScripting();
		
		PersoNJ hooded = (PersoNJ) persoUtils.persoByName("hooded");
		Assert.assertNotNull(hooded);
		Assert.assertEquals(new Point(93, 146), new Point(hooded.x, hooded.y));
	}
}
