/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

package zildo.fwk.script.xml.element;

import org.w3c.dom.Element;

public abstract class AnyElement {

	enum XmlElementKind {
		adventure(AdventureElement.class),
		scene(SceneElement.class),
		actions(ActionsElement.class),
		quest(QuestElement.class),
		mapscript(MapscriptElement.class),
		condition(ConditionElement.class);
		
		Class<? extends AnyElement> clazz;
		
		private XmlElementKind(Class<? extends AnyElement> p_clazz) {
			clazz = p_clazz;
		}
		
		public static XmlElementKind fromString(String p_name) {
			for (XmlElementKind x : values()) {
				if (x.name().equalsIgnoreCase(p_name)) {
					return x;
				}
			}
			throw new RuntimeException("Unable to find element "+p_name);
		}
	}
	
	protected Element xmlElement;
	
    public boolean waiting = false;
    public boolean done = false;

    /**
     * This method should set the "xmlElement" member variable, in order to have readAttribute, isTrue... get working.
     * @param p_elem
     */
    public abstract void parse(Element p_elem);
    
    // Useful operations
    public boolean isTrue(String p_attrName) {
    	String str=xmlElement.getAttribute(p_attrName);
    	return str.equalsIgnoreCase("true");
    }
    
    /**
     * Read an attribute's value, and return NULL if it isn't set.
     * @param p_attrName
     * @return String
     */
    public String readAttribute(String p_attrName) {
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
}