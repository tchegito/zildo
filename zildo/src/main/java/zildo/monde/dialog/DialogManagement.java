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

package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.ZUtils;
import zildo.fwk.net.TransferObject;
import zildo.fwk.script.model.ZSSwitch;
import zildo.fwk.script.xml.element.TriggerElement;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.quest.actions.BuyingAction;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

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
	 * @param p_persoToTalk perso to talk with (NULL if there's from an automatic behavior)
	 * @param p_actionDialog
	 */
	public void launchDialog(ClientState p_client, Perso persoToTalk, ActionDialog p_actionDialog ) {
		if (persoToTalk != null) {
			// Need to be done early because ZSSwitch need to know who is speaking
			p_client.zildo.setDialoguingWith(persoToTalk);
		}
	    WaitingDialog even = createWaitingDialog(p_client, persoToTalk, p_actionDialog);

	    if (even != null) {
			p_client.dialogState.continuing = even.sentence.indexOf("@") != -1;
			even.sentence = even.sentence.trim().replaceAll("@", "");
			even.sentence = even.sentence;
			
			dialogQueue.add(even);
			
        	
			if (persoToTalk == null && p_client.zildo.getDialoguingWith() != null) {
				// Zildo is talking with someone, and an automatic behavior happens (he takes an item).
				// After this automatic dialog, he should get back to the conversation.
				p_client.dialogState.continuing = true;
			}
	        p_client.dialogState.setDialoguing(true);
	    }

	}


	private String getPeopleName(String p_name) {
		String result = null;
		if (p_name != null) {
			// 1) Look for mapping
			String name = EngineZildo.scriptManagement.getReplacedPersoName(p_name);
			// 2) Look for translation
			result = UIText.getGameText("people."+name);
			if (result.startsWith("people.")) {
				result = name;	// Nothing found in translation
			}
			result = ZUtils.capitalize(result);
		}
		return result;
	}
	
	/**
	 * Returns a WaitingDialog object, ready to be added to the dialog queue.
	 * @param p_client
	 * @param persoToTalk (can't be null)
	 * @return WaitingDialog
	 */
	private WaitingDialog createWaitingDialog(ClientState p_client,
			Perso persoToTalk, ActionDialog actionDialog) {
		MapDialog dialogs = EngineZildo.mapManagement.getCurrentMap().getMapDialog();
		String sentence = null;
		String keySentence = null;
		currentSentenceFullDisplayed = false;

    	String whoSpeaking = null;
		if (persoToTalk != null) {
			// Dialog with character
			Behavior behav = dialogs.getBehaviors().get(persoToTalk.getName());
			if (behav == null) {
				// This perso can't talk, but trigger this although
				TriggerElement trig = TriggerElement.createDialogTrigger(persoToTalk.getName(), 1);
				EngineZildo.scriptManagement.trigger(trig);
				return null;
			}
			int compteDial = persoToTalk.getCompte_dialogue();

			// Dialog switch : adjust sentence according to quest elements
			// Do not evaluate if we're in a continuing sentence
			if (persoToTalk.getDialogSwitch() != null && !p_client.dialogState.continuing) {
				ZSSwitch swi = ZSSwitch.parseForDialog(persoToTalk.getDialogSwitch());
				int posSentence = swi.evaluateInt();
				if (posSentence > compteDial) {
					compteDial = posSentence;
				}
			}
			keySentence = dialogs.getSentence(behav, compteDial);

			sentence = UIText.getGameText(keySentence);

			// Update perso about next sentence he(she) will say
			int posSharp = sentence.indexOf("#");
			int posDollar = sentence.indexOf("$sell(");
			if (posSharp != -1) {
				// La phrase demande explicitement de rediriger vers une autre
				persoToTalk.setCompte_dialogue(sentence.charAt(posSharp + 1) - 48);
				sentence = sentence.substring(0, posSharp);
			} else if (posDollar != -1) {
				// This sentence leads to a buying phase
				String sellDescription = sentence.substring(posDollar+6, sentence.indexOf(")"));
				sentence = sentence.substring(0, posDollar);
				p_client.dialogState.actionDialog = new BuyingAction(p_client.zildo, persoToTalk, sellDescription);
			} else if (compteDial < 9 && behav.replique[compteDial + 1] != 0) {
				// On passe à la suivante, puisqu'elle existe
				persoToTalk.setCompte_dialogue(compteDial + 1);
			}

			// Adventure trigger
			TriggerElement trig = TriggerElement.createDialogTrigger(persoToTalk.getName(), compteDial);
			EngineZildo.scriptManagement.trigger(trig);

			// Set the dialoguing states for each Perso
			persoToTalk.setDialoguingWith(p_client.zildo);

			whoSpeaking = persoToTalk.getName();
		} else if (actionDialog != null) {
			// persoToTalk == null
		    // Ingame event
		    keySentence = actionDialog.key;
		    sentence = UIText.getGameText(keySentence);
		    whoSpeaking = actionDialog.who;
		    p_client.dialogState.actionDialog = actionDialog;
		}
		whoSpeaking = getPeopleName(whoSpeaking);

		// Dialog history
		String mapName = EngineZildo.mapManagement.getCurrentMap().getName();
		EngineZildo.game.recordDialog(keySentence, whoSpeaking, mapName);

		return new WaitingDialog(whoSpeaking, sentence, null, false, p_client == null ? null
				: p_client.location);
	}
	
	public void continueDialog(ClientState p_client) {
	    WaitingDialog even = createWaitingDialog(p_client, p_client.zildo.getDialoguingWith(), null);
	    boolean continuing = false;
	    if (even != null) {
		    continuing = even.sentence.indexOf("@") != -1;
			even.sentence = even.sentence.trim();
			even.action = CommandDialog.CONTINUE;
			dialogQueue.add(even);
	    }
	    p_client.dialogState.continuing = continuing;
	}
	
	/**
	 * Stop a dialog, when user press key, or brutally when zildo gets hurt.
	 * @param p_client
	 * @param p_brutal TRUE=Zildo leaves brutally his interlocutor
	 */
	public void stopDialog(ClientState p_client, boolean p_brutal) {
		p_client.dialogState.setDialoguing(false);
		PersoPlayer zildo=p_client.zildo;
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
		dialogQueue.add(new WaitingDialog(null, null, p_actionDialog, false, p_location));
	}
	
	public void writeConsole(String p_sentence) {
		dialogQueue.add(new WaitingDialog(null, p_sentence, null, true, null));
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