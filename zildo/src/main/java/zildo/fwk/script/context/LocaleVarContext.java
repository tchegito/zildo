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

package zildo.fwk.script.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import zildo.Zildo;
import zildo.fwk.collection.IdGenerator;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public abstract class LocaleVarContext implements IEvaluationContext {

	public final static String VAR_IDENTIFIER = "loc:";	// Prefix for local variables
	public final static String VAR_FUNC_IDENTIFIER = "loc:arg";	// Prefix for variables received in an execution
	
	// Global container for all local variables mapping
	static private IdGenerator localVariableNaming = new IdGenerator(256);

	// Local variables declared in this context only
    public Set<String> involvedVariables = new HashSet<String>();
    
	// Map <"Asked Variable name", "Assigned variable name">
	Map<String, String> locales = new HashMap<String, String>();
	
	public String registerVariable(String name) {
		involvedVariables.add(name);
		int id = localVariableNaming.pop();
		String varName = VAR_IDENTIFIER + id;
		locales.put(name, varName);
		if (Zildo.infoDebugScriptVerbose) {
			System.out.println("Registering variable "+name + " with name "+varName);
		}
		return varName;
	}
	
	/** Called by an Element when it dies ==> ID becomes useless **/
	public static void unregisterId(String name) {
		if (name != null && name.startsWith(VAR_IDENTIFIER)) {
			int id = Integer.valueOf(name.substring(VAR_IDENTIFIER.length()));
			localVariableNaming.remove(id);
		}
	}
	
	public void terminate() {
		for (String varName : involvedVariables) {
    		unregisterVariable(varName);
    	}
		involvedVariables.clear();
	}
	
	/** Called when an executor comes to an end => local would never be use **/
	public void unregisterVariable(String name) {
		String transco = locales.get(name);
		unregisterId(transco);
		locales.remove(name);
		
		// Remove variable value from global container
		String value = EngineZildo.scriptManagement.getVarValue(transco);
		if (Zildo.infoDebugScriptVerbose) {
			System.out.println("Removing "+transco+" for "+name+" valued with "+value);
		}
		if (value != null) {	// local value created with 'spawn' are not in scriptManagement.variables (see why)
			EngineZildo.scriptManagement.getVariables().remove(transco);
		}
	}
	
	/** Returns TRUE if there's at least 1 local variable in this context. Just for optimization purpose **/
	public boolean hasVariables() {
		return !locales.isEmpty();
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
	
	/** Returns a context of same nature with unherited variables **/
	@Override
	public LocaleVarContext clone() {
		try {
			if (locales.size() > 0) {
				LocaleVarContext cloned = (LocaleVarContext) super.clone();
				// Following line allows us to separate inherited and new variables
				cloned.involvedVariables = new HashSet<String>();
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
