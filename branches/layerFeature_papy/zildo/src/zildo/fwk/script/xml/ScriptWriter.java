package zildo.fwk.script.xml;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import zildo.fwk.script.xml.element.LanguageElement;
import zildo.fwk.script.xml.element.SceneElement;
import zildo.fwk.script.xml.element.action.ActionElement;
import zildo.fwk.script.xml.element.action.ActionElement.ActionKind;

public class ScriptWriter {

    File file;

	public final static String[] columnNames = new String[] { "Action", "who", "pos", "what", "value", "text", "angle",
	"name", "type", "delta", "fx", "speed", "backward", "unblock", "unstoppable" };
    
    static public Document document;
    
    static {
	    try {
		DocumentBuilder sxb = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = sxb.newDocument();
	    } catch (ParserConfigurationException e) {
		throw new RuntimeException("Unable to create document");
	    }
    }
    
    public ScriptWriter(String p_filename) {
	file = new File(p_filename);
    }
    
    public void create(List<SceneElement> p_scenes) {
        try {

	    
	    // Scenes
	    Element root = document.createElement("adventure");
	    document.appendChild(root);
	    for (SceneElement scene : p_scenes) {
		Element sceneElement = document.createElement("scene");
		sceneElement.setAttribute("id", scene.id);
		for (LanguageElement elem : scene.actions) {
			//TODO : save LanguageElement too !!!
			if (elem instanceof ActionElement) {
				ActionElement action = (ActionElement) elem;
			    ActionKind kind = action.kind;
			    if (kind != null) {
					Element actionElement = document.createElement(action.kind.toString());
					// Append attributes
					for (String attr : ScriptWriter.columnNames) {
					    String value = action.readAttribute(attr);
					    if (value != null) {
						actionElement.setAttribute(attr, value);
					    }
					}
					sceneElement.appendChild(actionElement);
			    }
		    }
		}
		root.appendChild(sceneElement);
	    }
	    
	    // Save the file with indentation
            DOMSource source = new DOMSource(document);
            StreamResult result =  new StreamResult(System.out);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(source, result);
            
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }
}
