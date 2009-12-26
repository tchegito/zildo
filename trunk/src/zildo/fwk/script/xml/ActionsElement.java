package zildo.fwk.script.xml;

import java.util.List;

import org.w3c.dom.Element;

/**
 * @author eboussaton
 */
public class ActionsElement extends ActionElement {

    public List<ActionElement> actions;

    public ActionsElement() {
    	super(null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void parse(Element p_elem) {
        actions = (List<ActionElement>) ScriptReader.parseNodes(p_elem);
    }
}