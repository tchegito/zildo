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

public class AdventureElement extends AnyElement {

	List<SceneElement> scenes;
	List<QuestElement> quests;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		// TODO Auto-generated method stub

		scenes = (List<SceneElement>) ScriptReader.parseNodes(p_elem, "scene");
		quests = (List<QuestElement>) ScriptReader.parseNodes(p_elem, "quest");
	}

	/**
	 * Get the named scene, if it exists.
	 * @param p_name
	 * @return SceneElement
	 */
	public SceneElement getSceneNamed(String p_name) {
		for (SceneElement scene : scenes) {
			if (scene.id.equalsIgnoreCase(p_name)) {
				return scene;
			}
		}
		return null;
	}
	
	public List<QuestElement> getQuests() {
		return quests;
	}
}
