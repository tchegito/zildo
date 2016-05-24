package tools;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;

import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.fwk.script.xml.ScriptReader;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

/**
 * All server engine started, with a Zildo spawned.
 * 
 * @author evariste.boussaton
 *
 */
public class EngineScriptUT extends EngineUT {

		protected ScriptManagement scriptMgmt;
		
		@Override
		@Before
		public void setUp() {
			super.setUp();
			scriptMgmt = EngineZildo.scriptManagement;
			
			// Spawn a zildo no matter where
			spawnZildo(160, 100);
			
			// Initializes this because ScriptExecutor need it (but it SHOULDN'T ! This is supposed to be SERVER side)
			ClientEngineZildo.mapDisplay = new MapDisplay(null);
		}
		
		protected void loadXMLAsString(String string) throws Exception {
			InputStream stream = new ByteArrayInputStream(string.getBytes());
			scriptMgmt.getAdventure().merge(ScriptReader.loadStream(stream));		
		}
		
		/** Process frames waiting for a variable to the expected value **/
		protected void synchroVariable(String name, int value) {
			String strValue = value + ".0";
			while (!strValue.equals(scriptMgmt.getVarValue(name))) {
				renderFrames(1);
			}
		}
		
		@Override
		@After
		public void tearDown() {
			super.tearDown();
		}
}
