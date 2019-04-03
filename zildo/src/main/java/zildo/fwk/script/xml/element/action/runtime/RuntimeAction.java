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

package zildo.fwk.script.xml.element.action.runtime;

import java.util.List;

import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionsElement;
import zildo.fwk.script.xml.element.action.LookforElement;
import zildo.fwk.script.xml.element.action.LoopElement;
import zildo.fwk.script.xml.element.logic.VarElement;

/**
 * Represents an action runtime context. Because we don't want to modify {@link ActionElement} anymore.
 * That would lead to much problems.
 * 
 * @author Tchegito
 *
 */
public class RuntimeAction extends RuntimeModifiableElement {

	public boolean waiting = false;
	
	public final boolean var;

	public final boolean unblock;

    public int count, nextStep;	// Used for 'timer' and 'wait'
    
	public LanguageElement action;
	
	// For case that a list of action must be used (actions, lookFor, ...)
    public List<RuntimeAction> actions = null;
    
	public RuntimeAction(LanguageElement action) {
		// Note: actions from TimerElement will be created when timer will really be executed
		if (action.getClass() == ActionsElement.class) {
			actions = createActions(((ActionsElement)action).actions);
			unblock = false;
			var = false;
		} else if (action.getClass() == LoopElement.class) {
			actions = createActions(((LoopElement)action).actions);
			this.action = action;
			unblock = false;
			var = false;
		} else if (action.getClass() == LookforElement.class) {
			actions = createActions(((LookforElement)action).actions);
			this.action = action;
			unblock = false;
			var = false;
		} else if (action.getClass() == VarElement.class) {
			this.action = action;
			unblock = false;
			var = true;
		} else {
			this.action = action;
			unblock = action.unblock;
			var = VarElement.class.isAssignableFrom(action.getClass());
		}
	}
	
	public boolean isMultiple() {
		// Only for 'actions' tag. Especially not 'lookFor' !
		return actions != null && action == null;
	}
	
	@Override
	public String toString() {
		if (action == null) {
			return "actions";
		} else {
			String str = action.toString();
			if (actions != null) str += "{"+actions+"}";
			return str;
		}
	}
	

}
