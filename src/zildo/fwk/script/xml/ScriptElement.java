package zildo.fwk.script.xml;

import java.util.List;

import org.w3c.dom.Element;

public class ScriptElement extends AnyElement {

    public String id;
    public StartElement start;
    public List<ActionElement> actions;

    @Override
    @SuppressWarnings("unchecked")
    public void parse(Element p_elem) {
        id = p_elem.getAttribute("id");

        actions = (List<ActionElement>) ScriptReader.parseNodes(p_elem, "action", "actions");
        start = (StartElement) ScriptReader.parseNodes(p_elem, "start").get(0);
    }

}