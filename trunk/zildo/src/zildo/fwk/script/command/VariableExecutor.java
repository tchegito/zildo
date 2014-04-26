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

import zildo.fwk.script.logic.IEvaluationContext;
import zildo.fwk.script.model.ZSCondition;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.fwk.script.xml.element.logic.VarElement.ValueType;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class VariableExecutor {

	IEvaluationContext context;
	boolean locked;
	
	public VariableExecutor(boolean p_locked, IEvaluationContext p_context) {
		locked = p_locked;
		context = p_context;
	}
	
	public boolean render(VarElement p_elem) {
        boolean achieved = false;
        if (p_elem.waiting) {
            waitForEndAction(p_elem);
            achieved = p_elem.done;
        } else {
			switch (p_elem.kind) {
			case var:
				String objToSave;
				if (p_elem.typ == ValueType.sellingItems) {
					objToSave = p_elem.strValue;
				} else {
					objToSave = "" + p_elem.value.evaluate(context);
				}
				EngineZildo.scriptManagement.getVariables().put(p_elem.name, objToSave);
				achieved = true;
				break;
			case _if:
				boolean success = false;
				success = p_elem.expression != null && ZSCondition.TRUE.equals(p_elem.expression.evaluate());
				success |= p_elem.value != null && p_elem.value.evaluate(context) == 1f;
				if (success) {
	            	EngineZildo.scriptManagement.execute(p_elem.ifThenClause, false, null, false, context, locked);
				} else {
					achieved = true;
				}
				break;
			}
			p_elem.done = achieved;
			p_elem.waiting = !achieved;
        }
        return achieved;
	}
        
    private void waitForEndAction(VarElement p_elem) {
    	boolean achieved = false;
        switch (p_elem.kind) {
        case _if:
        	achieved = true;
        }
        p_elem.waiting = !achieved;
        p_elem.done = achieved;
    }
}
