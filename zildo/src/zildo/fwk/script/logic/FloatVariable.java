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

package zildo.fwk.script.logic;

import zildo.monde.Hasard;


/**
 * @author Tchegito
 *
 */
public class FloatVariable implements FloatASTNode {

	String variable;
	
	public FloatVariable(String p_variable) {
		variable = p_variable;
	}

	@Override
	public float evaluate(IEvaluationContext context) {
		// Built-in functions
		if (FloatExpression.RESERVED_WORD_RANDOM.equals(variable)) {
			return (float) Math.random();
		} else if (FloatExpression.RESERVED_WORD_DICE10.equals(variable)) {
			return Hasard.rand(10);
		} else {
			// Context specific
			return context.getValue(variable);
		}
	}
	
	@Override
	public String toString() {
		return variable;
	}

}
