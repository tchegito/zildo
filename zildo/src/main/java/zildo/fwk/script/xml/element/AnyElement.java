/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

package zildo.fwk.script.xml.element;

import java.util.List;

import org.w3c.dom.Element;

import zildo.fwk.script.logic.FloatExpression;
import zildo.fwk.script.xml.element.action.ActionsElement;
import zildo.fwk.script.xml.element.action.ForElement;
import zildo.fwk.script.xml.element.action.ListenElement;
import zildo.fwk.script.xml.element.action.LookforElement;
import zildo.fwk.script.xml.element.action.LoopElement;
import zildo.fwk.script.xml.element.action.SeqElement;
import zildo.fwk.script.xml.element.action.TimerElement;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;

public abstract class AnyElement {

	enum XmlElementKind {
		adventure(AdventureElement.class),
		scene(SceneElement.class),
		actions(ActionsElement.class),
		quest(QuestElement.class),
		mapscript(MapscriptElement.class),
		condition(ConditionElement.class),
		persoaction(ContextualActionElement.class),
		tileaction(ContextualActionElement.class),
		timer(TimerElement.class),
		loop(LoopElement.class),
		_for(ForElement.class),
		lookfor(LookforElement.class),
		var(VarElement.class),
		listen(ListenElement.class),
		seq(SeqElement.class);
		
		Class<? extends AnyElement> clazz;
		final String realName;
		
		private XmlElementKind(Class<? extends AnyElement> p_clazz) {
			clazz = p_clazz;
			String s = name();
			realName = s.startsWith("_") ? s.substring(1) : s;
		}
		
		public static XmlElementKind fromString(String p_name) {
			for (XmlElementKind x : values()) {
				if (x.realName.equalsIgnoreCase(p_name)) {
					return x;
				}
			}
			throw new RuntimeException("Unable to find element "+p_name);
		}
	}
	
	protected Element xmlElement;

    /**
     * This method should set the "xmlElement" member variable, in order to have readAttribute, isTrue... get working.
     * @param p_elem
     */
    protected abstract void parse(Element p_elem);
    
    public void parseAndClean(Element p_elem) {
    	parse(p_elem);
    	// Parsing is over so we won't need any DOM element now : free some memory
		if (!EngineZildo.game.editing) {
			xmlElement = null;
		}
    }
    
    // Useful operations
    public boolean isTrue(String p_attrName) {
    	String str=xmlElement.getAttribute(p_attrName);
    	return str.equalsIgnoreCase("true");
    }
    
    public Boolean readBoolean(String p_attrName) {
    	String str=xmlElement.getAttribute(p_attrName);
    	if (str.equalsIgnoreCase("true")) {
    		return true;
    	} else if (str.equalsIgnoreCase("false")) {
    		return false;
    	}
    	return null;
    }
    
    /**
     * Read an optional attribute's value, and return NULL if it isn't set.
     * @param p_attrName
     * @return String
     */
    public String readAttribute(String p_attrName) {
    	if (!xmlElement.hasAttribute(p_attrName)) {
    		return null;
    	}
    	String value = xmlElement.getAttribute(p_attrName);
    	return value;
    }
    
    /**
     * Read an attribute's value, and return NULL if it is set to an empty string.
     * @param p_attrName
     * @return String
     */
    public String readNonEmptyAttribute(String p_attrName) {
    	String value = xmlElement.getAttribute(p_attrName);
    	return "".equals(value) ? null : value;
    }
    
    /**
     * Read an int value. Returns 0 if null.
     * @param p_attrName
     * @return int
     */
    protected int readInt(String p_attrName, int... defaultValue) {
    	int def = 0;
    	if (defaultValue.length > 0) {
    		def = defaultValue[0];
    	}
		String strValue = readAttribute(p_attrName);
		if (strValue == null) {
			return def;
		} else {
			return Integer.valueOf(strValue);
		}
    }
    
    /**
     * Convenience method, when we want to read a different XML element.
     * @return int
     */
    protected int readInt(Element p_xmlElement, String p_attrName, int... defaultValue) {
    	Element save = xmlElement;
    	xmlElement = p_xmlElement;
    	int result = readInt(p_attrName, defaultValue);
    	this.xmlElement = save;
    	return result;
    }
    
    protected Point readPoint(String p_attrName) {
    	String strPos = readAttribute(p_attrName);
		if (strPos != null) {
			return Point.fromString(strPos);
		} else {
			return null;
		}
    }
    
    /**
     * Used for expression that we know to be immediate.
     * @param p_attrName
     * @param defaultValue
     * @return 
     */
    protected float evaluateFloat(String p_attrName, int... defaultValue) {
    	String str = readAttribute(p_attrName);
    	int def = 0;
    	if (defaultValue.length > 0) {
    		def = defaultValue[0];
    	}
    	if (str == null) {
    		return def;
    	}
        return (int) new FloatExpression(str).evaluate(null);
    }

    /**
     * Merge two elements of same kind. Not necessarily overrided.
     * @param elem
     */
    public void merge(AnyElement elem) {
    	// Default : empty
    }
    
    static public AnyElement newInstanceFromString(String p_name) {
    	XmlElementKind kind = XmlElementKind.fromString(p_name);
    	try {
    		return kind.clazz.newInstance();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Unable to find class " + kind.toString());
	    }
    }
    
    /** Here we provide a way of inserting element before another one, to introduce syntaxic sugar.
     * For example a 'for' statement need a variable in its context.
     * @return
     */
	public List<AnyElement> addSyntaxicSugarBefore() {
		return null;
	}
	
	/** If this method returns TRUE, current element won't be inserted. It's useful when an element actually defines a combination of many others.
	 * For example <seq> leads to blocks of [<perso>, <wait>]
	 * @return
	 */
	public boolean isPlaceHolder() {
		return false;
	}
}