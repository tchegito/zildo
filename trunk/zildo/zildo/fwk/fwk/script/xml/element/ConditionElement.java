package zildo.fwk.script.xml.element;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.ScriptReader;

public class ConditionElement extends AnyElement {

	public String mapName;
	ZSSwitch expression;
	List<ActionElement> actions;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		mapName = readAttribute(p_elem, "name");
		expression = new ZSSwitch(readAttribute(p_elem, "exp")+":1,0");	// 1 will be the right value
		actions = (List<ActionElement>) ScriptReader.parseNodes(p_elem);
	}

	public boolean isRight() {
		return expression.evaluate() == 1;
	}
	
	public List<ActionElement> getActions() {
		return actions;
	}
}
