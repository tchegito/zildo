/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

package zildo.fwk.script.xml;

import java.util.List;

import org.w3c.dom.Element;

public class QuestElement extends AnyElement {

	public String name;
	List<TriggerElement> triggers;
	List<ActionElement> actions;
	List<ActionElement> history;
	
	boolean both;	// TRUE=each trigger element must be done AT THE SAME TIME to launch the actions

	// 'done' is TRUE when zildo has accomplished that
	
	@Override
    @SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		 name = p_elem.getAttribute("name");
		 
		 Element triggerContainer=ScriptReader.getChildNamed(p_elem, "trigger");
		 Element actionContainer=ScriptReader.getChildNamed(p_elem, "action");
		 Element historyContainer=ScriptReader.getChildNamed(p_elem, "history");
	     triggers = (List<TriggerElement>) ScriptReader.parseNodes(triggerContainer);
	     actions = (List<ActionElement>) ScriptReader.parseNodes(actionContainer);
	     if (historyContainer != null) {
	    	 history = (List<ActionElement>) ScriptReader.parseNodes(historyContainer);
	     }
	     
	     both="true".equalsIgnoreCase(triggerContainer.getAttribute("both"));
	}

	public List<TriggerElement> getTriggers() {
		return triggers;
	}

	public List<ActionElement> getActions() {
		return actions;
	}
	
	public List<ActionElement> getHistory() {
		return history;
	}
	
	public boolean isTriggersBoth() {
		return both;
	}
	
	public String toString() {
		return name + "\ntriggers="+triggers+"\nactions="+actions;
	}
}