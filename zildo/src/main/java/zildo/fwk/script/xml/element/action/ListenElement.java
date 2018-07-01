package zildo.fwk.script.xml.element.action;

import org.xml.sax.Attributes;


public class ListenElement extends ActionsNestedElement {

	public ListenElement() {
    	super(ActionKind.listen);
	}
	
	@Override
	public void parse(Attributes p_elem) {
		super.parse(p_elem);

		who = readAttribute("who");
	}
}
