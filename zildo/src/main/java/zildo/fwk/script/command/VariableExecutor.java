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

package zildo.fwk.script.command;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.model.ZSCondition;
import zildo.fwk.script.xml.element.action.runtime.RuntimeAction;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.fwk.script.xml.element.logic.VarElement.ValueType;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class VariableExecutor extends RuntimeExecutor {

	boolean locked;
	
	public VariableExecutor(boolean p_locked, IEvaluationContext p_context, ScriptProcess p_caller) {
		super(p_caller);
		locked = p_locked;
		context = p_context;
	}
	
	public boolean render(RuntimeAction p_runtimeAction) {
        boolean achieved = false;
        VarElement p_elem = (VarElement) p_runtimeAction.action;
        if (p_runtimeAction.waiting) {
            waitForEndAction(p_runtimeAction);
            achieved = p_runtimeAction.done;
        } else {
			switch (p_elem.kind) {
			case var:
				String objToSave;
				if (p_elem.typ == ValueType._string) {
					// STRING variables
					if (isLocal(p_elem.strValue)) {	// This variable could be local "loc:...", defined or undefined yet
						objToSave = getVariableName(p_elem.strValue);
					}  else {
						objToSave = p_elem.strValue;
					}
				} else {
					// FLOAT variables (default)
					if (context == null) {	// In mapscript, we have no context, but variable assignment is allowed though
						objToSave = p_elem.value.toString();
					} else {
						objToSave = "" + p_elem.value.evaluate(context);
					}
				}
				String name = p_elem.name;
				if (isLocal(name)) {	// This variable could be local "loc:...", defined or undefined yet
					name = getVariableName(name);
					if (name == null || name.equals(p_elem.name)) {
						name = handleLocalVariable(p_elem.name);	// We define it
					}
				}
				EngineZildo.scriptManagement.putVarValue(name, objToSave);
				achieved = true;
				break;
			case _if:
				boolean success = false;
				success = p_elem.expression != null && ZSCondition.TRUE.equals(p_elem.expression.evaluate());
				success |= p_elem.value != null && p_elem.value.evaluate(context) == 1f;
				if (success) {
					executeSubProcess(p_elem.ifThenClause);
				} else {
					achieved = true;
				}
				break;
			}
			p_runtimeAction.done = achieved;
			p_runtimeAction.waiting = !achieved;
        }
        return achieved;
	}
        
    private void waitForEndAction(RuntimeAction p_runtimeAction) {
    	VarElement p_elem = (VarElement) p_runtimeAction.action;
    	boolean achieved = false;
        switch (p_elem.kind) {
        case _if:
        	achieved = true;
       	default:
       		break;
        }
        p_runtimeAction.waiting = !achieved;
        p_runtimeAction.done = achieved;
    }
}
