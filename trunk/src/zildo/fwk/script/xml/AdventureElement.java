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
