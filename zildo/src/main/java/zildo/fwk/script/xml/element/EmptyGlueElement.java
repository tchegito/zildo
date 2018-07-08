package zildo.fwk.script.xml.element;

import org.xml.sax.Attributes;

/** XML Element making glue between two nodes. For example, "action", "trigger" and "history" are 3 glue nodes between "quest" parent node and child nodes. **/
public class EmptyGlueElement extends AnyElement {

	AnyElement parent;
	String glueFor;
	
	@Override
	protected void parse(Attributes p_elem) {
	}

	@Override
	public void add(String node, AnyElement elem) {
		if ("parent".equals(node)) {	// 1) set parent node
			parent = elem;
		} else if (parent != null) {	// 2) add child node to parent
			parent.add(glueFor, elem);
		}
	}
	
	public void setGlueFor(String glueFor) {
		this.glueFor = glueFor;
	}
	
	@Override
	public boolean isGlue() {
		return true;
	}
}
