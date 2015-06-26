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
import java.util.List;
import java.util.Set;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.context.LocaleVarContext;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.server.EngineZildo;

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
    
	ScriptProcess caller;	// Script owning this executor
	
	public RuntimeExecutor(ScriptProcess caller) {
		this.caller = caller;
	}
	
	/** Returns a previously assigned variable's ID, or same if it isn't one local. **/
    protected String getVariableName(String name) {
    	String searchName = name;
    	if (isLocal(name)) {
    		searchName = context.getString(name);
    	}
    	return searchName;
    }
    
    /** Returns local variable value, of given name if it doesn't exist. **/
    protected String getVariableValue(String name) {
    	String varName = getVariableName(name);
    	
    	if (!varName.equals(name)) {
    		return EngineZildo.scriptManagement.getVarValue(varName);
    	}
    	return name;
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
    
    /** Executes a script and wait for its end **/
    public void executeSubProcess(List<LanguageElement> actions) {
    	EngineZildo.scriptManagement.execute(actions, false, null, false, context, false, caller);  
    }
    public void executeSubProcess(List<LanguageElement> actions, IEvaluationContext ctx) {
    	EngineZildo.scriptManagement.execute(actions, false, null, false, ctx, false, caller);  
    }
    
    /** Executes a script without waiting for its end **/
    public void executeSubProcessInParallel(List<LanguageElement> actions) {
    	EngineZildo.scriptManagement.execute(actions, false, null, false, context, false, null);    	
    }
}
