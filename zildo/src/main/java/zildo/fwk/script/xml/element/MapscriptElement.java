package zildo.fwk.script.xml.element;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.xml.ScriptReader;

public class MapscriptElement extends AnyElement {

	List<ConditionElement> conditions;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		conditions = (List<ConditionElement>) ScriptReader.parseNodes(p_elem);
	}

	public List<ConditionElement> getConditions() {
		return conditions;
	}
}
