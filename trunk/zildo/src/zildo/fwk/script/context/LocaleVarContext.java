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

package zildo.fwk.script.context;

import java.util.HashMap;
import java.util.Map;

import zildo.fwk.collection.IdGenerator;

/**
 * @author Tchegito
 *
 */
public abstract class LocaleVarContext implements IEvaluationContext {

	static private IdGenerator localVariableNaming = new IdGenerator(256);

	Map<String, String> locales = new HashMap<String, String>();
	
	public String registerVariable(String name) {
		int id = localVariableNaming.pop();
		String varName = "var:"+id;
		locales.put(name, varName);
		return varName;
	}
	
	public String getString(String key) {
		return locales.get(key);
	}

}
