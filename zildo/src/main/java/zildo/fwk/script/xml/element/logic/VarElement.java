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

package zildo.fwk.script.xml.element.logic;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;
import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.element.AnyElement;
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
	public List<LanguageElement> ifThenClause = ZUtils.arrayList();
	
	public VarElement(VarKind p_kind) {
		kind = p_kind;
	}

	@Override
	public void parse(Attributes p_elem) {
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

	@Override
	public void add(String node, AnyElement elem) {
		ifThenClause.add((LanguageElement) elem); 
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(kind.toString());
		sb.append(" ");
		if (name == null) {
			if (expression != null) {
				sb.append(expression);
			} else {
				sb.append(strValue);
			}
		} else {
			sb.append(name).append("=").append(value);
		}
		return sb.toString();
	}
	
	public static VarElement createVarAction(String var, String expr) {
		VarElement elem = new VarElement(VarKind.var);
		elem.name = var;
		elem.value = new FloatExpression(expr);
		return elem;
	}
}
