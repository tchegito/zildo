package tools;

import org.junit.After;
import org.junit.Before;

import zildo.fwk.script.context.SceneContext;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

/**
 * All server engine started, with a Zildo spawned.
 * 
 * @author evariste.boussaton
 *
 */
public abstract class EngineScriptUT extends EngineUT {

	protected ScriptManagement scriptMgmt;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		scriptMgmt = EngineZildo.scriptManagement;

		// Spawn a zildo no matter where
		spawnZildo(160, 100);
	}

	/** Process frames waiting for a variable to the expected value **/
	protected void synchroVariable(String name, int value) {
		String strValue = value + ".0";
		while (!strValue.equals(scriptMgmt.getVarValue(name))) {
			renderFrames(1);
		}
	}

	public void executeScene(String name) {
		scriptMgmt.execute(name, true, new SceneContext(), null);
	}

	public void executeSceneNonBlocking(String name) {
		scriptMgmt.execute(name, false, new SceneContext(), null);
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}
}
