package zildo.fwk.script.xml.element.action;

import java.util.List;

import org.xml.sax.Attributes;

import zildo.fwk.ZUtils;
import zildo.fwk.script.xml.element.AnyElement;
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

	public List<LanguageElement> actions = ZUtils.arrayList();

	@Override
	public void parse(Attributes p_elem) {
		xmlElement = p_elem;
	}
	
	public void add(String node, AnyElement elem) {
		actions.add((LanguageElement) elem);
	}
}
