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

enum Operator { 
	// NOT_EQUALS works only when preceding EQUALS
	// We can combine test operation like this
	// a=4 + a=5 : means "a==4 || a==5"
	// a=3 * b=1 : means "a==3 && b==1"
	SEPARATOR(","), ROUND("round"), MIN("min"), MAX("max"), PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), OR("|"), AND("&"), EQUALS("="), NOT_EQUALS("!"), 
	LESSER("<"), GREATER(">"), MODULO("%") ;
	
	String symbol;

	private Operator(String s) {
		symbol = s;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * Return TRUE if current operator has priority on the given one.
	 */
	public boolean hasPriority(Operator o) {
		return this.ordinal() > o.ordinal();
	}
}