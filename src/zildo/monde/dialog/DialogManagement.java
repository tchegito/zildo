package zildo.monde.dialog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import zildo.fwk.engine.EngineZildo;
import zildo.gui.GUIManagement;
import zildo.monde.persos.Perso;
import zildo.prefs.Constantes;

// DialogManagement.cpp: implementation of the DialogManagement class.
//
//////////////////////////////////////////////////////////////////////


public class DialogManagement {

	public static final int ACTIONDIALOG_ACTION=0;
	public static final int ACTIONDIALOG_UP=1;
	public static final int ACTIONDIALOG_DOWN=2;
	
	private int n_phrases;
	private int n_comportements;
	private boolean dialoguing;
	private boolean topicChoosing;
	private int n_topics;

	private String dialogs[]=new String[Constantes.MAX_DIALOG];
	private Map<String, Behavior> behaviors;
	private DialogTopic[] topics=new DialogTopic[Constantes.MAX_TOPICS];

	private String currentSentence;
	private int positionInSentence;
	private int numToScroll;
	private int selectedTopic;
	private int nProposedTopics;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public DialogManagement() {
		clearDialogs();
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
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// Call the right method to manage interaction between Zildo and his human relative
	///////////////////////////////////////////////////////////////////////////////////////
	public void manageDialog() {
		if (topicChoosing) {
			manageTopic();
		} else if (dialoguing) {
			manageConversation();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageConversation
	///////////////////////////////////////////////////////////////////////////////////////
	// Here, Zildo is talking with someone.We can :
	// -go forward into conversation
	// -select a sentence into multiple ones
	// -quit dialog
	///////////////////////////////////////////////////////////////////////////////////////
	void manageConversation() {
		GUIManagement guiManagement=EngineZildo.guiManagement;
	
		boolean entireMessageDisplay=guiManagement.isEntireMessageDisplay();
		boolean visibleMessageDisplay=guiManagement.isVisibleMessageDisplay();
	
		if (entireMessageDisplay || visibleMessageDisplay) {
			if (numToScroll!=0) {
				numToScroll--;
				if (!entireMessageDisplay) {
					guiManagement.scrollAndDisplayTextParts(positionInSentence,currentSentence);
				}
			}
		} else if (!visibleMessageDisplay ) {
			// Draw sentences slowly (word are appearing one after another)
			positionInSentence++;
			if (positionInSentence % 3 ==0 && (Math.random()*10)>7) {
				EngineZildo.soundManagement.playSoundFX("AfficheTexte");
			}
			guiManagement.displayTextParts(positionInSentence,currentSentence,(numToScroll!=0));
		}
	
		// Debugging thing
		/*
		GFXBasics* gfxBasics = (GFXBasics*) EngineZildo::getGFXBasics();
		gfxBasics.StartRendering();
		String sent="position=       ";
		String sent2="numToScroll=    ";
		String sent3="entire=      visible=    ";
		String sent4=currentSentence.subString(positionInSentence);
		sent.ConvIntToStr(positionInSentence,9);
		sent2.ConvIntToStr(numToScroll,13);
		sent3.ConvIntToStr((int)entireMessageDisplay,7);
		sent3.ConvIntToStr((int)visibleMessageDisplay,21);
		gfxBasics.aff_texte(0,100,sent);
		gfxBasics.aff_texte(0,120,sent2);
		gfxBasics.aff_texte(0,140,sent3);
		gfxBasics.aff_texte(0,160,sent4);
		gfxBasics.EndRendering();
		*/
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void manageTopic() {
		GUIManagement guiManagement=EngineZildo.guiManagement;
		guiManagement.displayTopics(selectedTopic);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// actOnDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// -We came here when player clicks ACTION, UP or DOWN
	// .Quit dialog
	// .Move on dialog
	// .Choose topic
	///////////////////////////////////////////////////////////////////////////////////////
	public void actOnDialog(int actionDialog) {
		GUIManagement guiManagement = EngineZildo.guiManagement;
		boolean entireMessageDisplay=guiManagement.isEntireMessageDisplay();
		boolean visibleMessageDisplay=guiManagement.isVisibleMessageDisplay();
	
		if (topicChoosing) {
			// Topic
			switch (actionDialog) {
			case ACTIONDIALOG_ACTION:
				guiManagement.setToRemove_dialoguing(true);
				topicChoosing=false;
				dialoguing=false;
				break;
			case ACTIONDIALOG_DOWN:
				if (selectedTopic != nProposedTopics - 1) {
					selectedTopic++;
				}
				break;
			case ACTIONDIALOG_UP:
				if (selectedTopic != 0) {
					selectedTopic--;
				}
				break;
			}
		} else if (dialoguing) {
			// Conversation
			if (entireMessageDisplay || visibleMessageDisplay) {
				// Two cases : continue or quit
				if (!entireMessageDisplay) {
					numToScroll=3;
				} else {
					// Quit dialog
					guiManagement.setToRemove_dialoguing(true);
					dialoguing=false;
				}
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : perso to talk with
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchDialog(Perso persoToTalk) {
		Behavior behav=behaviors.get(persoToTalk.getNom());
		if (behav == null) {
			// This perso couldn't talk
			return;
		}
		int compteDial=persoToTalk.getCompte_dialogue();
		
		currentSentence=getSentence(behav,compteDial);
		EngineZildo.guiManagement.setText(currentSentence, GUIManagement.DIALOGMODE_CLASSIC);
		EngineZildo.guiManagement.setToDisplay_dialoguing(true);
		positionInSentence=0;
		dialoguing=true;
	
		// Update perso about next sentence he(she) will say
		String sharp="#";
		int posSharp=currentSentence.indexOf(sharp);
		if (posSharp != -1) {
			// La phrase demande explicitement de rediriger vers une autre
			persoToTalk.setCompte_dialogue(currentSentence.charAt(posSharp+1) - 48);
		} else if (behav.replique[compteDial+1]!=0) {
			// On passe à la suivante, puisqu'elle existe
			persoToTalk.setCompte_dialogue(compteDial + 1);
		} else if ("tigrou".equals(persoToTalk.getNom())) {
			persoToTalk.beingWounded(0,0);
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchTopicSelection
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchTopicSelection() {
		String currentSentence="La disparition\nLe mauvais temps\nLa dispute entre Henri et Lisa\nLa revolte des paysans\nLe prix des chaussures";
		EngineZildo.guiManagement.setText(currentSentence, GUIManagement.DIALOGMODE_TOPIC);
		positionInSentence=0;
		selectedTopic=0;
		nProposedTopics=5;
	
		topicChoosing=true;
		dialoguing=true;
	
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearDialogs
	///////////////////////////////////////////////////////////////////////////////////////
	public void clearDialogs() {
		n_phrases=0;
		n_comportements=0;
		n_topics=0;
		dialoguing=false;
		topicChoosing=false;
		positionInSentence=-1;
		numToScroll=0;
		behaviors=new LinkedHashMap<String, Behavior>();	// LinkedHashMap to keep order
	}

	public int getN_phrases() {
		return n_phrases;
	}

	public void setN_phrases(int n_phrases) {
		this.n_phrases = n_phrases;
	}

	public int getN_comportements() {
		return n_comportements;
	}

	public void setN_comportements(int n_comportements) {
		this.n_comportements = n_comportements;
	}

	public boolean isDialoguing() {
		return dialoguing;
	}

	public void setDialoguing(boolean dialoguing) {
		this.dialoguing = dialoguing;
	}

	public boolean isTopicChoosing() {
		return topicChoosing;
	}

	public void setTopicChoosing(boolean topicChoosing) {
		this.topicChoosing = topicChoosing;
	}

	public int getN_topics() {
		return n_topics;
	}

	public void setN_topics(int n_topics) {
		this.n_topics = n_topics;
	}
	
	public String[] getDialogs() {
		return dialogs;
	}
	
	public Map<String, Behavior> getBehaviors() {
		return behaviors;
	}
}