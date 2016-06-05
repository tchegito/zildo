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

package zildo.monde.sprites.persos.action;

import zildo.fwk.script.context.IEvaluationContext;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
public class ScriptedPersoAction implements PersoAction {

	Perso perso;
	
	public ScriptedPersoAction(Perso p_perso, String p_actionName) {
		this(p_perso, p_actionName, null);
	}
	
	public ScriptedPersoAction(Perso p_perso, String p_actionName, IEvaluationContext context) {
		perso = p_perso;
		
		EngineZildo.scriptManagement.runPersoAction(perso, p_actionName, context);
	}
	
	@Override
	public boolean launchAction() {
		return perso.getAttente() == 0;
	}

}
