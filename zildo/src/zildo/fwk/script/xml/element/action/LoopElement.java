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

package zildo.fwk.script.xml.element.action;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.ZUtils;
import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * @author Tchegito
 *
 */
public class LoopElement extends ActionElement {

	public FloatExpression whileCondition;	// default: infinite loop

	public List<LanguageElement> actions;

	public LoopElement() {
    	super(null);
    	kind = ActionKind.loop;
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;

		actions = (List<LanguageElement>) ScriptReader.parseNodes(xmlElement);
		
		String whenValue = xmlElement.getAttribute("when");
		whileCondition = ZUtils.isEmpty(whenValue) ? new FloatExpression(1) : new FloatExpression(whenValue);
		
		if (actions.isEmpty()) {
			throw new RuntimeException("Loop is empty !");
		}
	}
}
