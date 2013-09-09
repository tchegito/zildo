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

package zildo.fwk.script.xml.element.logic;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * @author Tchegito
 *
 */
public class VarElement extends LanguageElement {

	public enum VarKind {
		var, _if;
	}
	
	public VarKind kind;
	public String name;
	public FloatExpression value;
	
	@Override
	public void parse(Element p_elem) {
		super.parse(p_elem);
		
		kind = VarKind.valueOf(p_elem.getNodeName());
		name = readAttribute("name");
		value = new FloatExpression(readAttribute("value"));
	}

}
