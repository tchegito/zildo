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

package zildo.fwk.script.xml.element;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.action.ActionElement;

/**
 * Handle an action linked to a given character (Perso).<br/>
 * 
 * It consists of 3 parts, each one containing a list of {@link ActionElement} :<ol>
 * <li><b>&lt;start&gt;</b> indicating the whole action duration</li>
 * <li><b>&lt;time&gt;</b> will trigger the action list at given time rate</li>
 * <li><b>&lt;end&gt;</b> launch actions when duration is over</li>
 * </ol>
 * @author Tchegito
 *
 */
public class ContextualActionElement extends AnyElement {

	public String id;
	public int intervalle;
	public int endAttente;
	public int duration;
	
	public List<LanguageElement> actions;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;
		
		id = readAttribute("id");
		duration = readInt("duration", -1);	// Default is -1, which means infinite
		
		actions = (List<LanguageElement>) ScriptReader.parseNodes(xmlElement);
	}

}
