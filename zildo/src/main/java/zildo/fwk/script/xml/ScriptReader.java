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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import zildo.fwk.script.xml.element.AdventureElement;
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
	            System.out.println("Loading stream "+filename);
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
		//DocumentBuilder sxb = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		SAXParserFactory _f = SAXParserFactory.newInstance();
		SAXParser _p = _f.newSAXParser();
		AdventureHandler handler = new AdventureHandler();
		_p.parse(p_stream, handler);
		AnyElement root = handler.getRoot();
		return root;
	}
	
	static class AdventureHandler extends DefaultHandler {

		AdventureElement root;
		List<AnyElement> currentNodes;
		
		LinkedList<AnyElement> depth = new LinkedList<AnyElement>();
		
		String node;
		
		public AdventureElement getRoot() {
			return root;
		}
		@Override
		public void startElement(String namespaceURI, String lname,
				String qname, Attributes attrs) throws SAXException {
			node = qname;
			// Create node and add it to its parent if possible
			currentNodes = createNode(qname, attrs);
			if (!depth.isEmpty()) {
				for (AnyElement e : currentNodes) {
					if (e.isGlue()) {	// Declare parent
						e.add("parent", depth.getFirst());
					} else {
						depth.getFirst().add(null, e);
					}
				}
			}
			depth.addFirst(currentNodes.get(currentNodes.size()-1));
			
			if (qname.equalsIgnoreCase("adventure")) {
				root = (AdventureElement) currentNodes.get(0);
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) {
			if (Arrays.asList("quest", "scene", "mapScript", "persoAction", "tileAction").contains(qName)) {
				root.add(qName, depth.getFirst());
			}
			// Container end
			if ("condition".equalsIgnoreCase(qName)) {
				currentNodes.clear();
				currentNodes.add(depth.getFirst());
			}
			depth.getFirst().validate();
			depth.removeFirst();
			if (!depth.isEmpty() && !currentNodes.isEmpty()) {
				if (Arrays.asList("condition").contains(qName)) {
					depth.getFirst().add(qName, currentNodes.get(0));
				}
			}
		}
	}
    /**
     * Create an understandable element from the given node.
     * @param p_element
     * @return AnyElement
     */
    private static List<AnyElement> createNode(String name, Attributes p_element) {
        AnyElement s = null;
        // Check for ActionElement
        ActionKind kind=ActionKind.fromString(name);
        // Exclude specific actions
        if (kind != null && kind != ActionKind.actions && kind != ActionKind.timer
        		&&   		kind != ActionKind.loop && kind != ActionKind.lookFor && kind != ActionKind._for
        		&& 			kind != ActionKind.listen && kind != ActionKind.seq) { 
        	s=new ActionElement(kind);
        } else {
            QuestEvent event=QuestEvent.fromString(name);
        	if (event != null) {	// Trigger ?
	        	s=new TriggerElement(event);
	        } else {
	            VarKind varKind=VarKind.fromString(name);
	            if (varKind != null) {	// Var ?
	            	s = new VarElement(varKind);
	            } else {
		        	// General case
			        String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
		            s = AnyElement.newInstanceFromString(capitalizedName);
	            }
            }
        }
        s.parseAndClean(p_element);
        if (s.isGlue()) s.setGlueFor(name);
        List<AnyElement> result = new ArrayList<AnyElement>(2);
        if (!s.isPlaceHolder()) {
        	result.add(s);
        }
        List<AnyElement> before = s.addSyntaxicSugarBefore();
        if (before !=null) {
        	result.addAll(0, before);
        }
        return result;
    }

}