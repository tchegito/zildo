package zildo.fwk.script.xml;

import org.w3c.dom.Element;

public abstract class AnyElement {

    public boolean waiting = false;
    public boolean done = false;

    public abstract void parse(Element p_elem);
}