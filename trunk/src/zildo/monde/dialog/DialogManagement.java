/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.net.TransferObject;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.quest.actions.BuyingAction;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

// DialogManagement.cpp: implementation of the DialogManagement class.
//
//////////////////////////////////////////////////////////////////////


public class DialogManagement {
	
	List<WaitingDialog> dialogQueue;
	
	private boolean currentSentenceFullDisplayed;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public DialogManagement() {
		dialogQueue=new ArrayList<WaitingDialog>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// IN : p_zildo : 
	//      persoToTalk : 
	///////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Launch an interaction between Zildo and a character, or ingame event: <ul>
	 * <li>if <code>p_persoToTalk</code> is not null, starts a dialog with him.</li>
	 * <li>if <code>p_actionDialog</code> is not null, displays text and launch delegate action.</li>
	 * </ul>
	 * @param p_client Zildo who's talking
	 * @param p_persoToTalk perso to talk with
	 * @param p_actionDialog
	 */
	public void launchDialog(ClientState p_client, Perso persoToTalk, ActionDialog p_actionDialog ) {
	    WaitingDialog even = createWaitingDialog(p_client, persoToTalk);

	    if (even != null) {
		if (persoToTalk == null) {
		    // Ingame event
		    even.sentence = p_actionDialog.text;
		    p_client.dialogState.actionDialog = p_actionDialog;
		}
		p_client.dialogState.continuing = even.sentence.indexOf("@") != -1;
		even.sentence = even.sentence.trim().replaceAll("@", "");
		dialogQueue.add(even);
	    }
        	
            p_client.dialogState.dialoguing = true;
            p_client.zildo.setDialoguingWith(persoToTalk);
	}


	/**
	 * Returns a WaitingDialog object, ready to be added to the dialog queue.
	 * @param p_client
	 * @param persoToTalk (can't be null)
	 * @return WaitingDialog
	 */
	private WaitingDialog createWaitingDialog(ClientState p_client, Perso persoToTalk) {
		MapDialog dialogs=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		String sentence=null;
		currentSentenceFullDisplayed = false;
		
        	if (persoToTalk != null) {
        	    // Dialog with character
        	    Behavior behav = dialogs.getBehaviors().get(persoToTalk.getName());
        	    if (behav == null) {
        		// This perso can't talk
        		return null;
        	    }
        	    int compteDial = persoToTalk.getCompte_dialogue();
        
        	    // Dialog switch : adjust sentence according to quest elements
        	    if (persoToTalk.getDialogSwitch() != null) {
        		ZSSwitch swi = new ZSSwitch(persoToTalk.getDialogSwitch());
        		int posSentence = swi.evaluate();
        		if (posSentence > compteDial) {
        		    compteDial = posSentence;
        		}
        	    }
        	    sentence = dialogs.getSentence(behav, compteDial);
        
        	    sentence = UIText.getGameText(sentence);
        
        	    // Update perso about next sentence he(she) will say
        	    int posSharp = sentence.indexOf("#");
        	    int posDollar = sentence.indexOf("$");
        	    if (posSharp != -1) {
        		// La phrase demande explicitement de rediriger vers une autre
        		persoToTalk
        			.setCompte_dialogue(sentence.charAt(posSharp + 1) - 48);
        		sentence = sentence.substring(0, posSharp);
        	    } else if (posDollar != -1) {
        		// This sentence leads to a buying phase
        		sentence = sentence.substring(0, posDollar);
        		p_client.dialogState.actionDialog = new BuyingAction(
        			p_client.zildo, persoToTalk);
        	    } else if (behav.replique[compteDial + 1] != 0) {
        		// On passe à la suivante, puisqu'elle existe
        		persoToTalk.setCompte_dialogue(compteDial + 1);
        	    }
        
        	    // Adventure trigger
        	    TriggerElement trig = TriggerElement.createDialogTrigger(
        		    persoToTalk.getName(), compteDial);
        	    EngineZildo.scriptManagement.trigger(trig);
        
        	    // Set the dialoguing states for each Perso
        	    persoToTalk.setDialoguingWith(p_client.zildo);
        	}
        
        	return new WaitingDialog(sentence, null, false, p_client == null ? null : p_client.location);	    
	}
	
	public void continueDialog(ClientState p_client) {
	    WaitingDialog even = createWaitingDialog(p_client, p_client.zildo.getDialoguingWith());
	    p_client.dialogState.continuing = even.sentence.indexOf("@") != -1;

	    if (even != null) {
		even.sentence = even.sentence.trim();
		even.action = CommandDialog.CONTINUE;
		dialogQueue.add(even);
	    }
	}
	
	/**
	 * Stop a dialog, when user press key, or brutally when zildo gets hurt.
	 * @param p_client
	 * @param p_brutal TRUE=Zildo leaves brutally his interlocutor
	 */
	public void stopDialog(ClientState p_client, boolean p_brutal) {
		p_client.dialogState.dialoguing=false;
		PersoZildo zildo=p_client.zildo;
		Perso perso=p_client.zildo.getDialoguingWith();
		zildo.setDialoguingWith(null);
		if (perso != null) {
			perso.setDialoguingWith(null);
		}
		ActionDialog actionDialog=p_client.dialogState.actionDialog;
		if( p_brutal) {
			actOnDialog(p_client.location, CommandDialog.STOP);
		} else if (actionDialog != null) {
			actionDialog.launchAction(p_client);
			p_client.dialogState.actionDialog=null;
		}
	}

	public void goOnDialog(ClientState p_client) {
	    if (p_client.dialogState.continuing && currentSentenceFullDisplayed) {
	    	continueDialog(p_client);
	    } else {
	    	actOnDialog(p_client.location, CommandDialog.ACTION);
	    }
	}
	
	public void actOnDialog(TransferObject p_location, CommandDialog p_actionDialog) {
		dialogQueue.add(new WaitingDialog(null, p_actionDialog, false, p_location));
	}
	
	public void writeConsole(String p_sentence) {
		dialogQueue.add(new WaitingDialog(p_sentence, null, true, null));
	}
	
	public List<WaitingDialog> getQueue() {
		return dialogQueue;
	}
	
	public void resetQueue() {
		dialogQueue.clear();
	}

	public void setFullSentenceDisplayed() {
	    currentSentenceFullDisplayed = true;
	}
}