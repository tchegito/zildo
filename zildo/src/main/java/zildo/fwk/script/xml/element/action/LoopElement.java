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

package zildo.fwk.script.xml.element.action;

import org.w3c.dom.Element;

import zildo.fwk.ZUtils;
import zildo.fwk.script.logic.FloatExpression;

/**
 * @author Tchegito
 *
 */
public class LoopElement extends ActionsNestedElement {

	public FloatExpression whileCondition;	// default: infinite loop


	public LoopElement() {
    	super(ActionKind.loop);
    }
	
	public LoopElement(ActionKind kind) {
		super(kind);
	}
	
	@Override
	public void parse(Element p_elem) {
		super.parse(p_elem);
		
		String whenValue = xmlElement.getAttribute("when");
		whileCondition = ZUtils.isEmpty(whenValue) ? new FloatExpression(1) : new FloatExpression(whenValue);
		
		if (actions.isEmpty()) {
			throw new RuntimeException("Loop is empty !");
		}
	}
}
