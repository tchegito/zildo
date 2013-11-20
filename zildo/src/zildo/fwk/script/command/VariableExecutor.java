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
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.fwk.script.xml.element.logic.VarElement.ValueType;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class VariableExecutor {

	IEvaluationContext context;
	public VariableExecutor(IEvaluationContext p_context) {
		context = p_context;
	}
	
	public void render(VarElement p_elem) {
		switch (p_elem.kind) {
		case var:
			String objToSave;
			if (p_elem.typ == ValueType.sellingItems) {
				objToSave = p_elem.strValue;
			} else {
				objToSave = "" + p_elem.value.evaluate(context);
			}
			EngineZildo.scriptManagement.getVariables().put(p_elem.name, objToSave);
			break;
		}
	}
}
