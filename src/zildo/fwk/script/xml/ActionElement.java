/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk.script.xml;

import org.w3c.dom.Element;

import zildo.monde.map.Point;

public class ActionElement extends AnyElement {

    public enum ActionKind {
        actions, pos, moveTo, speak, script, angle, wait, sound, fadeIn, fadeOut, map, focus, spawn, exec, take, mapReplace, music;
        
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
    public boolean backward=false;	// Character moves back
    public boolean open=false;		// Can open doors
    public boolean delta=false;		// for 'moveTo' and 'pos' : means that 'pos' should be added to current location
    public ActionKind kind;
    public Point location;
    public String text;
    public int val;
    public float speed;

    private Element xmlElement;
    
    public ActionElement(ActionKind p_kind) {
    	kind = p_kind;
    	xmlElement = ScriptWriter.document.createElement(kind.toString());
    }
    
    @Override
    public void parse(Element p_elem) {
        if (kind == null) {
        	throw new RuntimeException("Action kind is unknown !");
        }
        
        // Store XML element in order to read easier from ZEditor
        xmlElement = p_elem;
        
    	// Read common attributes
        who = readAttribute(p_elem, "who");
        what = readAttribute(p_elem, "what");
        fx = p_elem.getAttribute("fx");
        unblock = "true".equalsIgnoreCase(p_elem.getAttribute("unblock"));
        speed = Float.valueOf("0"+p_elem.getAttribute("speed"));
        // Read less common ones
        String strPos=p_elem.getAttribute("pos");
        String strAngle=p_elem.getAttribute("angle");
        switch (kind) {
        case spawn:
            location = Point.fromString(strPos);
            if (!"".equals(strAngle)) {
            	val = Integer.valueOf(strAngle);
            }
            text = p_elem.getAttribute("type");
            break;
        case speak:
            text = p_elem.getAttribute("text");
            break;
        case sound:
        case map:
        case music:
            // String
            text = p_elem.getAttribute("name");
            break;
        case moveTo:
            backward = isTrue(p_elem, "backward");
            open = isTrue(p_elem, "open");
        case pos:
            // Position
            location = Point.fromString(strPos);
            delta = isTrue(p_elem, "delta");
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
        case take:
        	text = p_elem.getAttribute("item");
        	break;
        case exec:
        	text = p_elem.getAttribute("script");
        	break;
        case mapReplace:
        	text = p_elem.getAttribute("name");
        	break;
        }
    }
    
    /**
     * Returns the attribute's value from the XML representation. (used in ZEditor)
     * @param p_name
     * @return String
     */
    public String readAttribute(String p_name) {
	return readAttribute(xmlElement, p_name);
    }
    
    /**
     * Update attribute's value. (used only for ZEditor)
     * @param p_name
     * @param p_value
     */
    public void setAttribute(String p_name, String p_value) {
	xmlElement.setAttribute(p_name, p_value);
    }
    
}