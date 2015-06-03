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

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * Particular action : timer.<br/>
 * 
 * Consists of a set of actions to run every 'each' frame, a stop condition, and another set of actions
 * to execute when end condition is reached.
 * 
 * @author Tchegito
 *
 */
public class TimerElement extends ActionElement {

	public FloatExpression each;
	public FloatExpression endCondition;

	public List<LanguageElement> actions;
	public List<LanguageElement> end;

	public TimerElement() {
    	super(null);
    	kind = ActionKind.timer;
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;
		
			//TimerElement(int each, List<ActionElement> actions, String endCondition, List<ActionElement> end) {
		Element actionsContainer = ScriptReader.getChildNamed(p_elem, "action");
		Element endContainer = ScriptReader.getChildNamed(p_elem, "end");

		actions = (List<LanguageElement>) ScriptReader.parseNodes(actionsContainer);
		end = (List<LanguageElement>) ScriptReader.parseNodes(endContainer);
		
		each = new FloatExpression(readAttribute("each"));
		if (endContainer != null) {
			endCondition = new FloatExpression(endContainer.getAttribute("when"));
		}
		
		if (actions.isEmpty()) {
			throw new RuntimeException("Timer is empty !");
		}
	}
}
