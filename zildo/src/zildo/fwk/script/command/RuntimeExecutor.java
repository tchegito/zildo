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

package zildo.fwk.script.command;

import java.util.HashSet;
import java.util.Set;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;

/**
 * Abstract classes both implemented by {@link ActionExecutor} and {@link VariableExecutor}.
 * 
 * Here we handle common part, like local variables.
 * 
 * @author Tchegito
 *
 */
public abstract class RuntimeExecutor {
    
    Set<String> involvedVariables;
    
	IEvaluationContext context;
    
	/** Returned a previously assigned variable's ID, or same if it isn't one local. **/
    protected String getVariableName(String name) {
    	String searchName = name;
    	if (isLocal(name)) {
    		searchName = context.getString(name);
    	}
    	return searchName;
    }
    
    /** Assigns and return variable ID **/
    protected String handleLocalVariable(String name) {
    	String result = name;
    	if (isLocal(name)) {
    		result = context.registerVariable(name);
    		if (involvedVariables == null) {
    			involvedVariables = new HashSet<String>();
    		}
			involvedVariables.add(name);
    	}
    	return result;
    }
    
    protected boolean isLocal(String name) {
    	return context != null && name != null && name.startsWith(LocaleVarContext.VAR_IDENTIFIER);
    }
}
