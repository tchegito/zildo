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

package zildo.fwk.script.xml.element;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionKind;
import zildo.fwk.script.xml.element.action.TriggersElement;

public class QuestElement extends AnyElement {

	public String name;
	List<TriggerElement> triggers = ZUtils.arrayList();
	List<LanguageElement> actions = ZUtils.arrayList();
	List<LanguageElement> history = ZUtils.arrayList();

	boolean both; // TRUE=each trigger element must be done AT THE SAME TIME to
					// launch the actions
	boolean repeat; // TRUE=can be accomplished unlimited time
	public boolean locked;	// TRUE=block the game (default) / FALSE=player can move during script

	// 'done' is TRUE when zildo has accomplished that
	public boolean done = false;	// Only runtime modifiable field
	
	@Override
	public void parse(Attributes p_elem) {
		xmlElement = p_elem;
		
		name = readOrEmpty("name");
/*
		Element triggerContainer = ScriptReader.getChildNamed(p_elem, "trigger");
		Element actionContainer = ScriptReader.getChildNamed(p_elem, "action");
		Element historyContainer = ScriptReader.getChildNamed(p_elem, "history");
		triggers = (List<TriggerElement>) ScriptReader.parseNodes(triggerContainer);
		actions = (List<LanguageElement>) ScriptReader.parseNodes(actionContainer);
		if (historyContainer != null) {
			history = (List<LanguageElement>) ScriptReader.parseNodes(historyContainer);
		}
*/
		both = isTrue("both");
		repeat = isTrue("repeat");
		locked = !"false".equals(readAttribute("locked"));	// Default is false
		
		if (repeat) {
			// Add a final action to reset this quest (it must be "repeatable")
			ActionElement actionResetQuest = new ActionElement(ActionKind.markQuest);
			actionResetQuest.text = name;
			actionResetQuest.val = 0;
			actions.add(actionResetQuest);
		}
	}

	public void add(String node, AnyElement elem) {
		if ("action".equals(node)) {
			actions.add((ActionElement) elem);
		} else if ("trigger".equals(node)) {
			TriggersElement actions = (TriggersElement) elem;
			triggers.addAll(actions.elements);
		} else if ("history".equals(node)) {
			history.add((ActionElement) elem);
		}
	}
	public List<TriggerElement> getTriggers() {
		return triggers;
	}

	public List<LanguageElement> getActions() {
		return actions;
	}

	public List<LanguageElement> getHistory() {
		return history;
	}

	public boolean isTriggersBoth() {
		return both;
	}

	@Override
	public String toString() {
		return name + "\ntriggers=" + triggers + "\nactions=" + actions;
	}
}