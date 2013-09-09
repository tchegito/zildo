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

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Tchegito
 *
 */
public class CheckVariablesHandling extends EngineScript {

	String basicXML="<adventure>"+
					" <scene id='test1'>"+
					"  <var name='stolenMoney' value='48'/>" +
					" </scene>"+
					"</adventure>";
	
	@Test
	public void assignation() throws Exception {
		
		loadXMLAsString(basicXML);
		
		scriptMgmt.execute("test1", false);
		scriptMgmt.render();
		
		Assert.assertEquals("48.0", scriptMgmt.getVariables().get("stolenMoney"));
	}
}
