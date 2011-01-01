/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

public class SceneElement extends AnyElement {

    public String id;
    public boolean restoreZildo;	// TRUE=at the end of the scene, we should restore the previous zildo's state
    public List<ActionElement> actions;

    @Override
    @SuppressWarnings("unchecked")
    public void parse(Element p_elem) {
        id = p_elem.getAttribute("id");
        restoreZildo = "true".equalsIgnoreCase(p_elem.getAttribute("restoreZildo"));
        actions = (List<ActionElement>) ScriptReader.parseNodes(p_elem);
    }

    public static SceneElement createScene(List<ActionElement> p_actions) {
    	SceneElement scene=new SceneElement();
    	scene.id="fromActions";
    	scene.actions=p_actions;
    	return scene;
    }
}