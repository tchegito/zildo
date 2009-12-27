package zildo.fwk.script.xml;

import org.w3c.dom.Element;

import zildo.monde.map.Point;

public class ActionElement extends AnyElement {

    public enum ActionKind {
        pos, moveTo, speak, script, angle, wait, sound, fadeIn, fadeOut, map, focus, spawn;
        
        public static ActionKind fromString(String p_name) {
        	for (ActionKind kind : values()) {
        		if (kind.toString().equalsIgnoreCase(p_name)) {
        			return kind;
        		}
        	}
        	return null;
        }
    }

    public String who; // Characters
    public String what; // Camera, elements
    public String fx;
    public boolean unblock;
    public boolean backward=false;
    public boolean open=false;
    public ActionKind kind;
    public Point location;
    public String text;
    public int val;
    public float speed;

    public ActionElement(ActionKind p_kind) {
    	kind = p_kind;
    }
    
    @Override
    public void parse(Element p_elem) {
        if (kind == null) {
        	throw new RuntimeException("Action kind is unknown !");
        }
    	// Read common attributes
        who = p_elem.getAttribute("who");
        what = p_elem.getAttribute("what");
        fx = p_elem.getAttribute("fx");
        unblock = "true".equalsIgnoreCase(p_elem.getAttribute("unblock"));
        speed = Float.valueOf("0"+p_elem.getAttribute("speed"));
        // Read less common ones
        String strPos=p_elem.getAttribute("pos");
        String strAngle=p_elem.getAttribute("angle");
        String strBackward=p_elem.getAttribute("forward");
        String strOpen=p_elem.getAttribute("open");
        switch (kind) {
        case spawn:
            location = Point.fromString(strPos);
            val = Integer.valueOf(strAngle);
            text = p_elem.getAttribute("type");
            break;
        case speak:
            text = p_elem.getAttribute("text");
            break;
        case sound:
        case map:
            // String
            text = p_elem.getAttribute("name");
            break;
        case moveTo:
            backward = strBackward.equalsIgnoreCase("true");
            open = strOpen.equalsIgnoreCase("true");
        case pos:
            // Position
            location = Point.fromString(strPos);
            break;
        case script:
        case angle:
        case wait:
        	val = Integer.valueOf(p_elem.getAttribute("value"));
        	break;
        case fadeIn:
        case fadeOut:
            // Integer
            val = Integer.valueOf(p_elem.getAttribute("type"));
            break;
        case focus:
            who = p_elem.getAttribute("name");
            break;
        }
    }
}