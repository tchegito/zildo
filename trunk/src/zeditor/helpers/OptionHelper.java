package zeditor.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import zeditor.core.Options;

/**
 * Classe de gesstion des paramètres et options de Zeditor
 * @author Drakulo
 */
public class OptionHelper {
	/**
	 * Méthode statique de chargement des paramètres.
	 * @return Map <String, String> : une Map contenant les paramètres
	 * @author Drakulo
	 */
	public static Map<String, String> load() {
		// On crée une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		org.jdom.Document document;
		org.jdom.Element racine;
		Map<String, String> map = new HashMap<String, String>();
		try {
			// On charge le fichier de configuration
			File config = new File("config.xml");
			if (!config.exists()) {
				save(new HashMap<String, String>());
				return load();
			}
			// On crée un nouveau document JDOM avec en argument le fichier XML
			document = sxb.build(config);

			// On initialise un nouvel élément racine avec l'élément racine du
			// document.
			racine = document.getRootElement();

			// Mantenant qu'on a la racine, on récupère les infos
			if(racine.getChildren() != null || !racine.getChildren().isEmpty()){
				for(Options item : Options.values()){
					map.put(item.getValue(), racine.getChild(item.getValue()).getText());
				}
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Méthode statique de sauvegarde des paramétrages (appelée dans la fenêtre
	 * d'options)
	 * @param p_params
	 * @author Drakulo
	 */
	public static void save(Map<String, String> p_params) {
		try {
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur;
			constructeur = fabrique.newDocumentBuilder();
			Document document = constructeur.newDocument();
			// Création du noeud racine
			Element racine = document.createElement("config");
			// Affectation du noeud racine au document
			document.appendChild(racine);

			// On crée maintenant chacun des éléments de paramétrage
			for(Options name : Options.values()){
				Element modele = (Element) document.createElement(name.getValue());
				String item = (p_params.get(name.getValue()) != null) ? p_params.get(name.getValue()) : "";
				modele.appendChild(document.createTextNode(item));
				racine.appendChild(modele);
			}

			// On sauvegarde le fichier de config
			saveXml(document, "./config.xml");

		} catch (ParserConfigurationException e) {
			// Erreur possible lors de la construction du DocumentBuilder ?!
			e.printStackTrace();
		}
	}

	/**
	 * Méthode privée de sauvegarde du fichier de configuration
	 * @param document Document : le document à sauvegarder
	 * @param fichier String : le chemin du fichier dans lequel on va sauvegarder les informations
	 * @author Drakulo
	 */
	private static void saveXml(Document document, String fichier) {
		try {
			// On crée le source DOM
			Source source = new DOMSource(document);

			// On crée le fichier XML
			Result resultat = new StreamResult(new File(fichier));

			// On configure le transformeur
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			// On envoie la sauce
			transformer.transform(source, resultat);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Méthode de chargement du paramètre passé en paramètres
	 * @param p_option : Entrée de l'énumétation Options à charger
	 * @return String : Valeur du paramètre
	 * @author Drakulo
	 */
	public static String loadOption(String p_option) {
		Map<String, String> map = load();
		if(map != null && !map.isEmpty()){
			return map.get(p_option);
		}else{
			return null;
		}
	}
	
	/**
	 * Méthode de sauvegarde unitaire d'une option
	 * @param p_option : Entrée de l'énumétation Options à modifier
	 * @param p_value : Valeur à sauvegarder
	 * @author Drakulo
	 */
	public static void saveOption(String p_option, String p_value){
		Map<String, String> map = load();
		map.put(p_option, p_value);
		save(map);
	}
}
