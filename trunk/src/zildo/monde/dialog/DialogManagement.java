package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.net.TransferObject;
import zildo.monde.WaitingDialog;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.ClientState;
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
	public void launchDialog(ClientState p_client, Perso persoToTalk) {
		MapDialog dialogs=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		
		Behavior behav=dialogs.getBehaviors().get(persoToTalk.getNom());
		if (behav == null) {
			// This perso can't talk
			return;
		}
		int compteDial=persoToTalk.getCompte_dialogue();
		
		String sentence=dialogs.getSentence(behav,compteDial);
		dialogQueue.add(new WaitingDialog(sentence, -1, false, p_client.location));
		
		// Update perso about next sentence he(she) will say
		String sharp="#";
		int posSharp=sentence.indexOf(sharp);
		if (posSharp != -1) {
			// La phrase demande explicitement de rediriger vers une autre
			persoToTalk.setCompte_dialogue(sentence.charAt(posSharp+1) - 48);
		} else if (behav.replique[compteDial+1]!=0) {
			// On passe à la suivante, puisqu'elle existe
			persoToTalk.setCompte_dialogue(compteDial + 1);
		}
		// Set the dialoguing states for each Perso
		persoToTalk.setDialoguingWith(p_client.zildo);
		p_client.zildo.setDialoguingWith(persoToTalk);
		p_client.dialogState.dialoguing=true;
	}
	
	public void stopDialog(ClientState p_client) {
		p_client.dialogState.dialoguing=false;
		PersoZildo zildo=p_client.zildo;
		Perso perso=p_client.zildo.getDialoguingWith();
		perso.setDialoguingWith(null);
		zildo.setDialoguingWith(null);
	}
	public void actOnDialog(TransferObject p_location, int p_actionDialog) {
		dialogQueue.add(new WaitingDialog(null, p_actionDialog, false, p_location));
	}
	
	public void writeConsole(String p_sentence) {
		dialogQueue.add(new WaitingDialog(p_sentence, 0, true, null));
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