/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

package zildo.fwk.script.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.fwk.script.context.IEvaluationContext;

/**
 * Zildo-Script Condition.
 * 
 * @author Tchegito
 * 
 */
public class ZSCondition {

	final List<ZSExpression> expressions;
	final String result; // Awaited result when all expressions are TRUE

	static final String FALSE = "-999"; // Value which means "expression is false"
	public static final String TRUE = "1";
	
	public ZSCondition(int p_result) {
		result = "" + p_result;
		expressions = new ArrayList<ZSExpression>();
	}

	public ZSCondition(String p_result, List<ZSExpression> p_expressions) {
		this(p_result);
		expressions.addAll(p_expressions);
	}

	/**
	 * Fast constructor.
	 * 
	 * @param p_parseableString
	 *            expected to be "<expression>:<value>"
	 */
	public ZSCondition(String p_parseableString) {
		String[] strExpr = p_parseableString.split(":");
		if (strExpr.length != 2) {
			throw new RuntimeException(
					"Condition should be <expression>:<value>");
		}
		result = strExpr[1];
		expressions = ZSExpression.parse(strExpr[0]);
	}

	/**
	 * Constructor designed for fast build.
	 * 
	 * @param p_result
	 * @param p_parseableExpression
	 */
	public ZSCondition(int p_result, String p_parseableExpression) {
		this(p_result);
		String[] questsName = p_parseableExpression.split("&");
		for (String quest : questsName) {
			expressions.add(new ZSExpression(quest));
		}
	}

	public String evaluate() {
		return evaluate(null);
	}
	
	public String evaluate(IEvaluationContext context) {
		for (ZSExpression expr : expressions) {
			if (!expr.isTrue(context)) {
				return FALSE;
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<ZSExpression> it = expressions.iterator(); it.hasNext();) {
			ZSExpression e = it.next();
			sb.append(e.toString());
			if (it.hasNext()) {
				sb.append("&");
			}
		}
		sb.append(":").append(result);
		return sb.toString();
	}
}