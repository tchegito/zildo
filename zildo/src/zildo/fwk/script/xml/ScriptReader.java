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

package zildo.fwk.script.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import zildo.fwk.script.xml.element.AnyElement;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionKind;
import zildo.fwk.script.xml.element.logic.VarElement;
import zildo.fwk.script.xml.element.logic.VarElement.VarKind;
import zildo.monde.quest.QuestEvent;

public class ScriptReader {

    /**
     * Return a script element containing all the hierarchy from a given XML file.
     * @param p_scriptName
     * @return AnyElement
     */
    public static AnyElement loadScript(String... p_scriptNames) {
        AnyElement ret = null;
    	for (String scriptName : p_scriptNames) {
    		try {
	            // Load the stream
	            String filename = "zildo/resource/script/"+scriptName+".xml";
	            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);
	            if (stream == null) {
	            	stream = ScriptReader.class.getClassLoader().getResourceAsStream(filename);
	            	if (stream == null) { // Ultimate check: used for Unit Test
	            		stream = ScriptReader.class.getClassLoader().getResourceAsStream(scriptName+".xml");
	            	}
	            }
	            
	            AnyElement root = loadStream(stream);
	            
	            if (ret == null) {
	            	ret = root;
	            } else {
	            	// Append read nodes to the previous one
	            	ret.merge(root);
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("Unable to parse " + scriptName, e);
	        }
    	}
    	System.gc();
    	return ret;
    }

	public static AnyElement loadStream(InputStream p_stream) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilder sxb = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document document = sxb.parse(p_stream);
		Element racine = document.getDocumentElement();

		List<AnyElement> roots = createNode(racine);
		// We shouldn't have more than one here
		assert roots.size() == 1;
		AnyElement root = roots.get(0);
		return root;
	}
	
    /**
     * Create an understandable element from the given node.
     * @param p_element
     * @return AnyElement
     */
    private static List<AnyElement> createNode(Element p_element) {
        String name = p_element.getNodeName();
        AnyElement s = null;
        // Check for ActionElement
        ActionKind kind=ActionKind.fromString(name);
        // Exclude specific actions
        if (kind != null && kind != ActionKind.actions && kind != ActionKind.timer && kind != ActionKind.loop && kind != ActionKind.lookFor && kind != ActionKind._for) { 
        	s=new ActionElement(kind);
        } else {
            QuestEvent event=QuestEvent.fromString(name);
        	if (event != null) {	// Trigger ?
	        	String questName = ((Element)p_element.getParentNode().getParentNode()).getAttribute("name");
	        	s=new TriggerElement(event, questName);
	        } else {
	            VarKind varKind=VarKind.fromString(name);
	            if (varKind != null) {	// Var ?
	            	s = new VarElement(varKind);
	            } else {
		        	// General case
			        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
		            s = AnyElement.newInstanceFromString(name);
	            }
            }
        }
        s.parseAndClean(p_element);
        List<AnyElement> result = new ArrayList<AnyElement>(2);
        result.add(s);
        List<AnyElement> before = s.addSyntaxicSugarBefore();
        if (before !=null) {
        	result.addAll(0, before);
        }
        return result;
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
                    elements.addAll(createNode((Element) node));
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