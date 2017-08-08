package zildo.fwk.script.xml.element.action;

import org.w3c.dom.Element;


public class ListenElement extends ActionsNestedElement {

	public ListenElement() {
    	super(ActionKind.listen);
	}
	
	@Override
	public void parse(Element p_elem) {
		super.parse(p_elem);

		who = readAttribute("who");
	}
}
