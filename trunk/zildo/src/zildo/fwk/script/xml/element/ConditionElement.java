package zildo.fwk.script.xml.element;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.model.ZSCondition;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.ScriptReader;

public class ConditionElement extends AnyElement {

	String mapName;
	ZSSwitch expression;	// No expression means it's always verified
	List<LanguageElement> actions;
	Boolean scroll;
	
	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;
		
		mapName = readAttribute("name");
		scroll = readBoolean("scroll");
		String strExp = readAttribute("exp");
		expression = strExp == null ? null : ZSSwitch.parseForScript(strExp);	// 1 will be the right value
		actions = (List<LanguageElement>) ScriptReader.parseNodes(p_elem);
	}

	public boolean match(String p_mapName, boolean p_scroll) {
		// 1) map
		if (mapName != null && !mapName.equals(p_mapName))
			return false;
		// 2) expression
		if (expression != null && !expression.evaluate().equals(ZSCondition.TRUE))
			return false;
		// 3) scroll
		if (scroll != null && p_scroll != scroll) 
			return false;
		return true;
	}
	
	public List<LanguageElement> getActions() {
		return actions;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("map:").append(mapName);
		sb.append(", if:").append(expression);
		sb.append(", then:").append(actions);
		return sb.toString();
	}
}
