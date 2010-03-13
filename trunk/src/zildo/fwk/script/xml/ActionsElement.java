/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

/**
 * @author eboussaton
 */
public class ActionsElement extends ActionElement {

    public List<ActionElement> actions;

    public ActionsElement() {
    	super(null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void parse(Element p_elem) {
        actions = (List<ActionElement>) ScriptReader.parseNodes(p_elem);
    }
}