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

package zildo.fwk.script.logic;

import zildo.fwk.script.context.IEvaluationContext;

/**
 * @author Tchegito
 *
 */
public class FloatOperator implements FloatASTNode{

	FloatASTNode operand1;
	FloatASTNode operand2;
	Operator op;
	
	public FloatOperator(Operator p_op, FloatASTNode p_n1, FloatASTNode p_n2) {
		operand1 = p_n1;
		operand2 = p_n2;
		op = p_op;
	}
	
	public float evaluate(IEvaluationContext c) {
		float f1 = operand1.evaluate(c);
		float f2 = operand2.evaluate(c);

		switch (op) {
			case PLUS:
				return f1 + f2;
			case MINUS:
				return f1 - f2;
			case MULTIPLY:
				return f1 * f2;
			case DIVIDE:
			default:
				return f1 / f2;
			case MODULO:
				return f1 % f2;
			// Comparison
			case EQUALS:
				return f1 == f2 ? 1 : 0;
			case NOT_EQUALS:
				return f1 != f2 ? 1 : 0;
			case LESSER:
				return f1 < f2 ? 1 : 0;
			case GREATER:
				return f1 > f2 ?1 : 0;
			case OR:
				return (int)f1 == 1 || (int)f2 == 1 ? 1 : 0;
			case AND:
				return f1 == 1 && f2 == 1 ? 1 : 0;
			case MIN:
				return Math.min(f1,  f2);
			case MAX:
				return Math.max(f1,  f2);
			case ROUND:
				return (int) f1;

		}
	}

	@Override
	public String toString() {
		switch (op) {
		case ROUND:	// 1 operand
			return op+"("+operand1.toString()+")";
		default:
			return op+"("+operand1.toString()+", "+operand2.toString()+")";
		}
	}
}
