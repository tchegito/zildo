package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.net.TransferObject;
import zildo.monde.WaitingDialog;
import zildo.monde.persos.Perso;
import zildo.server.EngineZildo;

// DialogManagement.cpp: implementation of the DialogManagement class.
//
//////////////////////////////////////////////////////////////////////


public class DialogManagement {
	
	private boolean dialoguing;
	private boolean topicChoosing;
	
	List<WaitingDialog> dialogQueue;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public DialogManagement() {
		clearDialogs();
		
		dialogQueue=new ArrayList<WaitingDialog>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : p_zildo : Zildo who's talking
	//      persoToTalk : perso to talk with
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchDialog(TransferObject p_location, Perso persoToTalk) {
		MapDialog dialogs=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		
		Behavior behav=dialogs.getBehaviors().get(persoToTalk.getNom());
		if (behav == null) {
			// This perso couldn't talk
			return;
		}
		int compteDial=persoToTalk.getCompte_dialogue();
		
		String sentence=dialogs.getSentence(behav,compteDial);
		dialogQueue.add(new WaitingDialog(sentence, p_location));
		
		// Update perso about next sentence he(she) will say
		String sharp="#";
		int posSharp=sentence.indexOf(sharp);
		if (posSharp != -1) {
			// La phrase demande explicitement de rediriger vers une autre
			persoToTalk.setCompte_dialogue(sentence.charAt(posSharp+1) - 48);
		} else if (behav.replique[compteDial+1]!=0) {
			// On passe à la suivante, puisqu'elle existe
			persoToTalk.setCompte_dialogue(compteDial + 1);
		} else if ("tigrou".equals(persoToTalk.getNom())) {
			persoToTalk.beingWounded(0,0);
		}
	}
	
	public void actOnDialog(int actionDialog) {
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchTopicSelection
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchTopicSelection() {
		//TODO : topic
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clearDialogs
	///////////////////////////////////////////////////////////////////////////////////////
	public void clearDialogs() {
		dialoguing=false;
		topicChoosing=false;
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
	
	public List<WaitingDialog> getQueue() {
		return dialogQueue;
	}
	
	public void resetQueue() {
		dialogQueue.clear();
	}

}