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

package zildo.fwk.script.xml.element.action.runtime;

import java.util.List;

import zildo.fwk.ZUtils;
import zildo.fwk.script.xml.element.LanguageElement;

/**
 * Just a class to propose 'done' as a modifiable boolean.
 * 
 * We DO NOT want that on ActionElement. Because, they can be instantiated from multiple context (timer, tileAction...)
 * and they can't share a 'done' status.
 * 
 * @author Tchegito
 *
 */
public abstract class RuntimeModifiableElement {

	// Only non-final field, modified at runtime
	// 'done' is TRUE when zildo has accomplished that (trigger, or quest)
	public boolean done = false;
	
	protected List<RuntimeAction> createActions(List<? extends LanguageElement> p_actions) {
		List<RuntimeAction> rtActions = ZUtils.arrayList();
		for (LanguageElement el : p_actions) {
			rtActions.add(new RuntimeAction(el));
		}
		return rtActions;
	}
}
