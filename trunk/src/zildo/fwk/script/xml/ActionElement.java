package zildo.fwk.script.xml;

import org.w3c.dom.Element;

import zildo.monde.map.Point;

public class ActionElement extends AnyElement {

    public enum ActionKind {
        pos, moveTo, speak, script, angle;
    }

    public String who;
    public ActionKind kind;
    public Point location;
    public String text;
    public int val;
    
    @Override
    public void parse(Element p_elem) {
        who = p_elem.getAttribute("who");
        String value = null;
        for (ActionKind k : ActionKind.values()) {
            value = p_elem.getAttribute(k.toString());
            if (!"".equals(value)) {
                kind = k;
                break;
            }
        }
        if (kind == null) {
            throw new RuntimeException("Action kind is unknown !");
        }
        switch (kind) {
            case speak:
                text = value;
                break;
            case moveTo:
            case pos:
                location = Point.fromString(value);
                break;
            case script:
            case angle:
            	val=Integer.valueOf(value);
            	break;
        }
    }
}