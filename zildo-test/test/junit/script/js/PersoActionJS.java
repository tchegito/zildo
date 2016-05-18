/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package junit.script.js;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import tools.EngineUT;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoSpider;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class PersoActionJS extends EngineUT {

	String displayLocationScript
			  ="function displayLocation(perso) {"+
			   "  return '(' + perso.x + ' ,' + perso.y + ')';"+
			   "}"+
			   "function move(perso) {"+
			   "  if (!mapMgmt.collide(perso.x+1, perso.y+1, perso)) {"+
			   "    perso.x++; perso.y++;"+
			   "  }"+
			   "}";
	
	String addProto = 
				"function PersoSpider(perso) {"+
				"  this.p"+
				" }"+
				"PersoSpider.prototype.displaykikoo = function() { return 'kikoo'; }";
	
	@Test
	public void simple() throws Exception {
	    ScriptEngineManager scriptManager = new ScriptEngineManager();
	    ScriptEngine engineJS = scriptManager.getEngineByName("JavaScript");
	    engineJS.eval(displayLocationScript);
	    engineJS.put("mapMgmt", EngineZildo.mapManagement);
	    Invocable engineInvocable = (Invocable) engineJS;
	    
	    Object result;
	    
	    Perso perso = new PersoSpider(160, 100);
	    result = engineInvocable.invokeFunction("move", perso);
	    System.out.println();

	    result = engineInvocable.invokeFunction("displayLocation", perso);
	    System.out.println(result);
	}
	
	@Test
	public void addProto() throws Exception {
		ScriptEngineManager scriptManager = new ScriptEngineManager();
	    ScriptEngine engineJS = scriptManager.getEngineByName("JavaScript");
	    engineJS.eval(addProto);
	    
	}
}
