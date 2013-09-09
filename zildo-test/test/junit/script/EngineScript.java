/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;

import zildo.fwk.script.xml.ScriptReader;
import zildo.monde.Game;
import zildo.server.EngineZildo;
import zildo.server.state.ScriptManagement;

/**
 * @author Tchegito
 *
 */
public class EngineScript {

	ScriptManagement scriptMgmt;
	
	@Before
	public void setUp() {
		EngineZildo.setGame(new Game(false));
		scriptMgmt = new ScriptManagement();
		EngineZildo.scriptManagement = scriptMgmt;
	}
	
	protected void loadXMLAsString(String string) throws Exception {
		InputStream stream = new ByteArrayInputStream(string.getBytes());
		scriptMgmt.getAdventure().merge(ScriptReader.loadStream(stream));		
	}
	
}
