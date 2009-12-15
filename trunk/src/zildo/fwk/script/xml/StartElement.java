package zildo.fwk.script.xml;

import java.util.List;

import org.w3c.dom.Element;

public class StartElement extends AnyElement {

	public String mapName;
	public List<ActionElement> startActions;
	
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		mapName=p_elem.getAttribute("map");
		
		startActions=(List<ActionElement>) ScriptReader.parseNodes(p_elem, "action");
	}
}
