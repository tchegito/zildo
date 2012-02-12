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

package zildo.fwk.script.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import zildo.fwk.script.xml.element.ActionElement;
import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.script.xml.element.ActionElement.ActionKind;
import zildo.monde.quest.QuestEvent;

public class ScriptReader {

    /**
     * Return a script element containing all the hierarchy from a given XML file.
     * @param p_scriptName
     * @return AnyElement
     */
    public static AnyElement loadScript(String p_scriptName) {
        AnyElement ret = null;
        try {
            DocumentBuilder sxb = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            // Load the stream
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("zildo/resource/script/"+p_scriptName);
            
            Document document = sxb.parse(stream);
            Element racine = document.getDocumentElement();
            
            ret = createNode(racine);

        } catch (Exception e) {
            throw new RuntimeException("Unable to parse " + p_scriptName, e);
        }
        return ret;
    }

    /**
     * Create an understandable element from the given node.
     * @param p_element
     * @return AnyElement
     */
    private static AnyElement createNode(Element p_element) {
        String name = p_element.getNodeName();
        AnyElement s = null;
        // Check for ActionElement
        ActionKind kind=ActionKind.fromString(name);
        QuestEvent event=QuestEvent.fromString(name);
        if (kind != null && kind != ActionKind.actions) { 
        	s=new ActionElement(kind);
        } else if (event != null) {
        	s=new TriggerElement(event);
        } else {
        	// General case
	        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
            s = AnyElement.newInstanceFromString(name);
        }
        s.parse(p_element);
        return s;
    }

    /**
     * Returns a list of every child node, which name is contained in the provided ones.
     * @param p_element
     * @param p_nodeName all acceptable node names (can be null => all childs will be taken)
     * @return List
     */
    public static List<? extends AnyElement> parseNodes(Element p_element, String... p_nodeName) {
        List<AnyElement> elements = new ArrayList<AnyElement>();
        if (p_element == null) {	// No child ? Returns an empty list.
        	return elements;
        }
        NodeList list = p_element.getChildNodes();
        List<String> acceptables = new ArrayList<String>();
        if (p_nodeName != null) {
            for (String s : p_nodeName) {
                acceptables.add(s.toUpperCase());
            }
        }
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeNameUpper = node.getNodeName().toUpperCase();
                if (acceptables.size() == 0 || acceptables.contains(nodeNameUpper)) {
                    elements.add(createNode((Element) node));
                }
            }
        }
        return elements;
    }
    
    public static Element getChildNamed(Element p_element, String p_nodeName) {
        NodeList list = p_element.getChildNodes();
        Element returnElement=null;
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeNameUpper = node.getNodeName().toUpperCase();
                
                if (p_nodeName.equalsIgnoreCase(nodeNameUpper)) {
                	returnElement=(Element) node;
                }
            }
        }
        return returnElement;
    }
}