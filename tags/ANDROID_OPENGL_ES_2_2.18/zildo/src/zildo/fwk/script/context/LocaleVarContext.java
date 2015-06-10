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

	public final static String VAR_IDENTIFIER = "loc:";
	
	static private IdGenerator localVariableNaming = new IdGenerator(256);

	// Map <"Asked Variable name", "Assigned variable name">
	Map<String, String> locales = new HashMap<String, String>();
	
	public String registerVariable(String name) {
		int id = localVariableNaming.pop();
		String varName = VAR_IDENTIFIER + id;
		locales.put(name, varName);
		return varName;
	}
	
	/** Called by an Element when it dies ==> ID becomes useless **/
	public static void unregisterId(String name) {
		if (name != null && name.startsWith(VAR_IDENTIFIER)) {
			int id = Integer.valueOf(name.substring(VAR_IDENTIFIER.length()));
			localVariableNaming.remove(id);
		}
	}
	
	/** Called when an executor comes to an end => local would never be use **/
	public void unregisterVariable(String name) {
		locales.remove(name);
	}
	
	public static void clean() {
		localVariableNaming.reset();
	}

	public String getString(String key) {
		return locales.get(key);
	}

	protected void cloneLocales(LocaleVarContext original) {
		locales = new HashMap<String, String>(original.locales);
	}
	
	@Override
	public LocaleVarContext clone() {
		try {
			if (locales.size() > 0) {
				LocaleVarContext cloned = (LocaleVarContext) super.clone();
				cloned.cloneLocales(this);
				return cloned;
			} else {
				return this;
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Unable to clone this context !");
		}
	};
}
