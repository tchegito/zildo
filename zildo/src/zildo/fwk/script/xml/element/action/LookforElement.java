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

import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.LanguageElement;
import zildo.monde.sprites.persos.Perso.PersoInfo;


/**
 * @author Tchegito
 *
 */
public class LookforElement extends ActionElement {

	public List<LanguageElement> actions;
	public int radius;
	public boolean negative;	// TRUE=execute nested actions if 'lookFor' fails
	public boolean sight;	// TRUE=consider only character's sight (=depending on his angle) / FALSE=consider the whole area around him
	public String desc;	// Not null means we look for sprite entity with given type. Otherwise, we consider only Perso
	
	public LookforElement() {
    	super(null);
    	kind = ActionKind.lookFor;
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;

		who = readAttribute("who");
		radius = readInt("radius");
		negative = Boolean.TRUE == readBoolean("negative");
		sight = Boolean.TRUE == readBoolean("sight");
		desc = readAttribute("type");
		String strInfo = readAttribute("info");
		if (strInfo != null) {
			info = PersoInfo.valueOf(strInfo);
		}
		
		actions = (List<LanguageElement>) ScriptReader.parseNodes(xmlElement);
	}
	
}
