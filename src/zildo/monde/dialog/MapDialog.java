package zildo.monde.dialog;

import java.util.LinkedHashMap;
import java.util.Map;

import zildo.prefs.Constantes;

public class MapDialog {

	private int n_phrases;
	private int n_comportements;
	private int n_topics;

	private String dialogs[]=new String[Constantes.MAX_DIALOG];
	private Map<String, Behavior> behaviors;
	private DialogTopic[] topics=new DialogTopic[Constantes.MAX_TOPICS];

	public MapDialog() {
		n_phrases=0;
		n_comportements=0;
		n_topics=0;
		behaviors=new LinkedHashMap<String, Behavior>();	// LinkedHashMap to keep order
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// addSentence
	///////////////////////////////////////////////////////////////////////////////////////
	// Add the given sentence to sentence buffer.
	///////////////////////////////////////////////////////////////////////////////////////
	public void addSentence(String sentence) {
		dialogs[n_phrases]=sentence;
		n_phrases++;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	///////////////////////////////////////////////////////////////////////////////////////
	public void addBehavior(String nomPerso, short[] replique) {
		Behavior b=new Behavior();
		b.persoName=nomPerso;
		for (int i=0;i<replique.length;i++) {
			b.replique[i]=replique[i];
		}
		behaviors.put(nomPerso, b);
		n_comportements++;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	///////////////////////////////////////////////////////////////////////////////////////
	void addTopic(String topicName) {
		// Initialize first one with ID=1;
		n_topics++;
		topics[n_topics]=new DialogTopic(n_topics,topicName);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getSentence
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name
	///////////////////////////////////////////////////////////////////////////////////////
	String getSentence(Behavior behav, int numComportement) {
		String reponse="pas trouve !";
		if (behav!=null) {
			int numSentence=behav.replique[numComportement];
			reponse=dialogs[numSentence - 1];
			//char a='#';
			//int posDiese=reponse.indexOf(a);
			//if (posDiese != -1) {
		}
		return reponse;
	}
	
	public int getN_phrases() {
		return n_phrases;
	}
	
	public String[] getDialogs() {
		return dialogs;
	}
	
	public Map<String, Behavior> getBehaviors() {
		return behaviors;
	}
}
