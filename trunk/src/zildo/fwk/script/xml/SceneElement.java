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