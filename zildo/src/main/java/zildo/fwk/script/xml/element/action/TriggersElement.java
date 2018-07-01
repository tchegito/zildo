package zildo.fwk.script.xml.element.action;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.TriggerElement;

public class TriggersElement extends AnyElement {

	public List<TriggerElement> elements = ZUtils.arrayList();

	@Override
	protected void parse(Attributes p_elem) {
		
	}
	
	@Override
	public void add(String node, AnyElement elem) {
		elements.add((TriggerElement) elem);
	}
	
}
