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

package zildo.monde.quest.actions;

import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

public class ScriptAction extends ActionDialog {

	public ScriptAction(String p_key) {
		super(p_key);
	}
	
	public ScriptAction(String p_key, String p_who) {
		super(p_key, p_who);
	}
	
	@Override
	public void launchAction(ClientState p_clientState) {
		EngineZildo.scriptManagement.userEndAction();
	}
}
