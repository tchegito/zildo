package zildo.fwk.script.xml.element;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;

public class MapscriptElement extends AnyElement {

	List<ConditionElement> conditions = ZUtils.arrayList();
	
	@Override
	public void parse(Attributes p_elem) {
	}

	@Override
	public void add(String name, AnyElement elem) {
		if ("condition".equals(name)) {
			conditions.add((ConditionElement) elem);
		}
	}
	public List<ConditionElement> getConditions() {
		return conditions;
	}
}
