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

import zildo.fwk.script.context.IEvaluationContext;
import zildo.monde.Hasard;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;


/**
 * @author Tchegito
 *
 */
public class FloatVariable implements FloatASTNode {

	@SuppressWarnings("serial")
	class NoContextException extends RuntimeException {
		
	}
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
		} else if (variable.startsWith(FloatExpression.RESERVED_WORD_ZILDO)) {
			if (EngineZildo.persoManagement == null) {
				throw new NoContextException();
			}
			Perso p = EngineZildo.persoManagement.getZildo();
			if (p == null) {
				throw new NoContextException();
			}
			if (FloatExpression.RESERVED_WORD_ZILDOX.equals(variable)) {
				return p.x;
			} else if (FloatExpression.RESERVED_WORD_ZILDOY.equals(variable)) {
				return p.y;
			} else if (FloatExpression.RESERVED_WORD_ZILDOMONEY.equals(variable)) {
				return p.getMoney();
			} else if (FloatExpression.RESERVED_WORD_ZILDOANGLEX.equals(variable)) {
				return p.getAngle().coords.x;
			} else if (FloatExpression.RESERVED_WORD_ZILDOANGLEY.equals(variable)) {
				return p.getAngle().coords.y;
			}
			return 0;	// Not understood variable
		} else {
			// Try global variables
			if (EngineZildo.scriptManagement != null) {
				String val = EngineZildo.scriptManagement.getVarValue(variable);
				if (val != null) {
					return new FloatExpression(val).evaluate(context);
				}
			}
			// Context specific
			if (context == null) {
				throw new NoContextException();
			}
			return context.getValue(variable);
		}
	}
	
	@Override
	public String toString() {
		return variable;
	}

}
