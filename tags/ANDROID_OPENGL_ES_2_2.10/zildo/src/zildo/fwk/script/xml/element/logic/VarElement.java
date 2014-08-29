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

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * @author Tchegito
 *
 */
public class VarElement extends LanguageElement {

	public enum VarKind {
		var, _if;
		
		@Override
		public String toString() {
			return this == _if ? "if" : super.toString();
		}
		
		public static VarKind fromString(String p_name) {
			for (VarKind kind : values()) {
				if (kind.toString().equalsIgnoreCase(p_name)) {
					return kind;
				}
			}
			return null;
		}
	}
	
	public enum ValueType {
		sellingItems, _float;
		
		@Override
		public String toString() {
			return this == _float ? "float" : super.toString();
		}
	}
	
	public VarKind kind;
	public String name;
	public FloatExpression value;
	public String strValue;	// Set only for non-float values
	public ZSSwitch expression;
	public ValueType typ = ValueType._float;
	public List<LanguageElement> ifThenClause;
	
	public VarElement(VarKind p_kind) {
		kind = p_kind;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		super.parse(p_elem);
		
		name = readAttribute("name");
		strValue = readAttribute("value");
		
		String typStr = readAttribute("type");
		if (typStr != null) {
			typ = ValueType.valueOf(typStr);
		}
		switch (kind) {
		case _if:
			strValue = readAttribute("expQuest");
			expression = strValue == null ? null : ZSSwitch.parseForScript(strValue);
			strValue = readAttribute("exp");
			if (strValue != null) {
				value = new FloatExpression(strValue);
			}
			ifThenClause = (List<LanguageElement>) ScriptReader.parseNodes(xmlElement); 
			break;
		default:
		case var:
			switch (typ) {
			case _float:
			default:
				value = new FloatExpression(strValue);
				break;
			case sellingItems:
				break;
			}
		}
	}

}
