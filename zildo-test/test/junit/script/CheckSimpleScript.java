/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package junit.script;

import junit.framework.TestCase;
import zildo.fwk.script.model.ZSSwitch;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

/**
 * Checks the {@link ZSSwitch} and linked classes.
 * @author Tchegito
 *
 */
public class CheckSimpleScript extends TestCase {

	ScriptManagement scriptMgmt;
	
	@Override
	public void setUp() {
		scriptMgmt = new ScriptManagement();
		EngineZildo.scriptManagement = scriptMgmt;
	}
	
	public void testSimpleSwitch() {
		// Create a switch case : case 'flut' => 8 / case 'enlevement' => 3 / default => 0
		ZSSwitch simple = new ZSSwitch(0).addCondition("flut", 8).addCondition("enlevement",3);
		
		checkSwitch(simple);
	}
	
	public void testParsedSwitch() {
		ZSSwitch parsed = ZSSwitch.parseForDialog("flut&!ferme:8,enlevement:3,0");
		
		checkSwitch(parsed);
	}
	
	private void checkSwitch(ZSSwitch sw) {
		// Default
		assertEquals(0, sw.evaluate());
		
		// enlevement
		scriptMgmt.accomplishQuest("enlevement", false);
		assertEquals(3, sw.evaluate());

		// flut
		scriptMgmt.accomplishQuest("flut", false);
		assertEquals(8, sw.evaluate());
	
	}
	
	@Override
	public void tearDown() {
		EngineZildo.scriptManagement = null;
	}
}
