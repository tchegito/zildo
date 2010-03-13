package zildo.fwk.script.xml;

import java.util.List;

import org.w3c.dom.Element;

public class QuestElement extends AnyElement {

	public String name;
	List<TriggerElement> triggers;
	List<ActionElement> actions;
	boolean both;	// TRUE=each trigger element must be done AT THE SAME TIME to launch the actions

	public boolean done=false;	// TRUE when zildo has accomplished that
	
	@Override
    @SuppressWarnings("unchecked")
	public void parse(Element p_elem) {
		 name = p_elem.getAttribute("name");
		 
		 Element triggerContainer=ScriptReader.getChildNamed(p_elem, "trigger");
		 Element actionContainer=ScriptReader.getChildNamed(p_elem, "action");
	     triggers = (List<TriggerElement>) ScriptReader.parseNodes(triggerContainer);
	     actions = (List<ActionElement>) ScriptReader.parseNodes(actionContainer);
	        
	     both="true".equalsIgnoreCase(triggerContainer.getAttribute("both"));
	}

	public List<TriggerElement> getTriggers() {
		return triggers;
	}
	

	public List<ActionElement> getActions() {
		return actions;
	}
	
	public boolean isTriggersBoth() {
		return both;
	}
}