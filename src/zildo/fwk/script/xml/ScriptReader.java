package zildo.fwk.script.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import zildo.fwk.script.xml.ActionElement.ActionKind;
import zildo.prefs.Constantes;

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
            // On charge le fichier de configuration
            File config = new File(Constantes.DATA_PATH + p_scriptName);

            Document document = sxb.parse(config);
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
        if (kind != null) { 
        	s=new ActionElement(kind);
        } else {
        	// General case
	        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()).toLowerCase();
	        name = "zildo.fwk.script.xml." + name + "Element";
	
	        try {
	            s = (AnyElement) Class.forName(name).newInstance();
	        } catch (Exception e) {
	            throw new RuntimeException("Unable to find class " + name);
	        }
        }
        s.parse(p_element);
        return s;
    }

    /**
     * Returns a list of every child node, which name is contained in the provided ones.
     * @param p_element
     * @param p_nodeName all acceptable node names
     * @return List
     */
    static List<? extends AnyElement> parseNodes(Element p_element, String... p_nodeName) {
        NodeList list = p_element.getChildNodes();
        List<AnyElement> elements = new ArrayList<AnyElement>();
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
}