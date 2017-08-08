package zildo.fwk.script.xml.element.action;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.xml.ScriptReader;
import zildo.fwk.script.xml.element.LanguageElement;

/** Simple action element, containing a list of actions.
 * Useful for sub-process execution, depending on condition (example: for, lookFor ...)
 * 
 * The subclass MUST call super.parse in its own parse method.
 * 
 */
public class ActionsNestedElement extends ActionElement {

	final String containerName;

	public ActionsNestedElement(ActionKind p_kind) {
		this(p_kind, null);
	}

	public ActionsNestedElement(ActionKind p_kind, String containerName) {
		super(p_kind);
		this.containerName = containerName;
	}

	public List<LanguageElement> actions;

	@Override
	@SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		xmlElement = p_elem;

		Element container = xmlElement;
		if (containerName != null) {
			container = ScriptReader.getChildNamed(p_elem, containerName);
		}

		actions = (List<LanguageElement>) ScriptReader.parseNodes(container);
	}
	
}
