package zildo.fwk.script.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import zildo.fwk.script.xml.element.AdventureElement;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionKind;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.fwk.script.xml.element.logic.VarElement.VarKind;
import zildo.monde.quest.QuestEvent;

public class AdventureHandler extends DefaultHandler {

	AdventureElement root;
	List<AnyElement> currentNodes;
	
	LinkedList<AnyElement> depth = new LinkedList<AnyElement>();
	
	String node;
	
	public AdventureElement getRoot() {
		return root;
	}
	@Override
	public void startElement(String namespaceURI, String lname,
			String qname, Attributes attrs) throws SAXException {
		node = qname;
		// Create node and add it to its parent if possible
		currentNodes = createNode(qname, attrs);
		if (!depth.isEmpty()) {
			for (AnyElement e : currentNodes) {
				if (e.isGlue()) {	// Declare parent
					e.add("parent", depth.getFirst());
				} else {
					depth.getFirst().add(null, e);
				}
			}
		}
		depth.addFirst(currentNodes.get(currentNodes.size()-1));
		
		if (qname.equalsIgnoreCase("adventure")) {
			root = (AdventureElement) currentNodes.get(0);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (Arrays.asList("quest", "scene", "mapScript", "persoAction", "tileAction").contains(qName)) {
			root.add(qName, depth.getFirst());
		}
		// Container end
		if ("condition".equalsIgnoreCase(qName)) {
			currentNodes.clear();
			currentNodes.add(depth.getFirst());
		}
		depth.getFirst().validate();
		depth.removeFirst();
		if (!depth.isEmpty() && !currentNodes.isEmpty()) {
			if (Arrays.asList("condition").contains(qName)) {
				depth.getFirst().add(qName, currentNodes.get(0));
			}
		}
	}
	
    /**
     * Create an understandable element from the given node.
     * @param p_element
     * @return AnyElement
     */
    private List<AnyElement> createNode(String name, Attributes p_element) {
        AnyElement s = null;
        // Check for ActionElement
        ActionKind kind=ActionKind.fromString(name);
        // Exclude specific actions
        if (kind != null && kind != ActionKind.actions && kind != ActionKind.timer
        		&&   		kind != ActionKind.loop && kind != ActionKind.lookFor && kind != ActionKind._for
        		&& 			kind != ActionKind.listen && kind != ActionKind.seq) { 
        	s=new ActionElement(kind);
        } else {
            QuestEvent event=QuestEvent.fromString(name);
        	if (event != null) {	// Trigger ?
	        	s=new TriggerElement(event);
	        } else {
	            VarKind varKind=VarKind.fromString(name);
	            if (varKind != null) {	// Var ?
	            	s = new VarElement(varKind);
	            } else {
		        	// General case
			        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
		            s = AnyElement.newInstanceFromString(capitalizedName);
	            }
            }
        }
        s.parseAndClean(p_element);
        if (s.isGlue()) s.setGlueFor(name);
        List<AnyElement> result = new ArrayList<AnyElement>(2);
        if (!s.isPlaceHolder()) {
        	result.add(s);
        }
        List<AnyElement> before = s.addSyntaxicSugarBefore();
        if (before !=null) {
        	result.addAll(0, before);
        }
        return result;
    }

}