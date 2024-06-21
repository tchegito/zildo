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

import org.xml.sax.Attributes;

import zildo.fwk.script.model.point.IPoint;
import zildo.monde.sprites.persos.Perso.PersoInfo;


/**
 * @author Tchegito
 *
 */
public class LookforElement extends ActionsNestedElement {

	public int radius;
	public boolean negative;	// TRUE=execute nested actions if 'lookFor' fails
	public boolean sight;	// TRUE=consider only character's sight (=depending on his angle) / FALSE=consider the whole area around him
	public String desc;	// Not null means we look for sprite entity with given type. Otherwise, we consider only Perso
	public boolean changeContext;
	

	public LookforElement() {
    	super(ActionKind.lookFor);
    }
	
	@Override
	public void parse(Attributes p_elem) {
		super.parse(p_elem);
		
		who = readAttribute("who");
		radius = readInt("radius");
		negative = Boolean.TRUE == readBoolean("negative");
		sight = Boolean.TRUE == readBoolean("sight");
		desc = readAttribute("type");
		Boolean cc = readBoolean("changeContext");
		changeContext = cc == null ? true : cc;
		String strInfo = readAttribute("info");
		if (strInfo != null) {
			info = PersoInfo.valueOf(strInfo);
		}
		String strPos = readOrEmpty("pos");
		if (!strPos.isEmpty()) {
			location = IPoint.fromString(strPos);
		}
		
		if (who == null && location == null) {
			throw new RuntimeException("'lookFor' action need either 'who' or 'pos' attribute to work !");
		}
	}
	
}
