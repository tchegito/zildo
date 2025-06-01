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

import zildo.fwk.script.context.IEvaluationContext;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.QuestElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.server.state.ScriptCall;

/**
 * Runtime representation of a scene, because {@link SceneElement} is the model, thus unmodifiable.
 * @author Tchegito
 *
 */
public class RuntimeScene extends RuntimeModifiableElement {

	public final String id;
	public final boolean locked;
	
	public final List<RuntimeAction> actions;
	public final SceneElement model;
	public boolean restoreZildo;
	public final ScriptCall call;	// origin of this scene's call (contains arguments if they exist)
    
    // Marker to identify that a scene is created from an 'action' quest's tag
    public final static String MARQUER_SCENE = "@scene@";
    
	public RuntimeScene(List<LanguageElement> p_actions, QuestElement p_quest, boolean p_locked, ScriptCall p_call) {
		actions = createActions(p_actions);
		if (p_quest != null) {
			id = MARQUER_SCENE+p_quest.name;
			locked = p_quest.locked;
		} else {
			id = "fromActions";
			locked = p_locked;
		}
		model = null;
		restoreZildo = false;
		call = p_call;
	}
	
	public RuntimeScene(SceneElement scene, boolean p_locked, ScriptCall p_call) {
		id = scene.id;
		actions = createActions(scene.actions);
		locked = p_locked;
		model = scene;
		restoreZildo = scene.restoreZildo;
		call = p_call;
	}

	public RuntimeScene(List<RuntimeAction> p_actions, boolean p_locked, ScriptCall p_call) {
		actions = p_actions;
		id = "fromActions";
		locked = p_locked;
		model = null;
		restoreZildo = false;
		call = p_call;
	}
	
	public void registerVariables(IEvaluationContext context, IEvaluationContext callerContext) {
		if (call != null) {
			call.registerVariables(context, callerContext);
		}
	}
	
	@Override
	public String toString() {
		return actions.toString();
	}
}
