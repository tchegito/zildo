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

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * Particular action : timer.<br/>
 * 
 * Consists of a set of actions to run every 'each' frame, a stop condition, and another set of actions
 * to execute when end condition is reached.
 * 
 * <pre>
 * <b>&lt;timer</b> each="FLOAT_EXPRESSION"&gt;
 *   <b>&lt;action&gt;
 *      ...
 *   &lt;/action&gt;</b>
 *   &lt;end when="FLOAT_EXPRESSION"&gt;
 *      ...
 *   &lt;/end&gt;
 * &lt;/timer&gt;
 * </pre>
 * @author Tchegito
 *
 */
public class TimerElement extends ActionsNestedElement {

	public FloatExpression each;
	public FloatExpression endCondition;

	public List<LanguageElement> end;

	public TimerElement() {
    	super(ActionKind.timer, "action");
    }
	
	@Override
	public void parse(Attributes p_elem) {
		super.parse(p_elem);
		
		unblock = isTrue("unblock");
		
		each = new FloatExpression(readAttribute("each"));
		/* TODO: how get this with SAX ???
		if (endContainer != null) {
			endCondition = new FloatExpression(endContainer.getAttribute("when"));
		}
		*/
	}
	
	@Override
	public void add(String node, AnyElement elem) {
		super.add(node, elem);
		if ("end".equals(node)) {
			end.add((LanguageElement) elem);
		}
	}

	@Override
	public void validate() {
		if (actions.isEmpty()) {
			throw new RuntimeException("Timer is empty !");
		}
	}
}
