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

package zildo.client.gui;

import java.util.List;

import zildo.client.ClientEngineZildo;
import zildo.client.sound.BankSound;
import zildo.monde.dialog.WaitingDialog;

public class DialogDisplay {

	public static final int ACTIONDIALOG_ACTION=0;
	public static final int ACTIONDIALOG_UP=1;
	public static final int ACTIONDIALOG_DOWN=2;
	
	public boolean dialoguing;
	public boolean topicChoosing;
	
	private String currentSentence;
	private int positionInSentence;
	private int numToScroll;
	private int selectedTopic;
	private int nProposedTopics;

	public DialogDisplay() {
		dialoguing=false;
	}
	
	public boolean isDialoguing() {
		return dialoguing;
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
	
	/**
	 * Act on a dialog. Launch, continue, or quit the dialog.
	 * @param p_queue
	 * @return boolean (TRUE if dialog ends)
	 */
    public boolean launchDialog(List<WaitingDialog> p_queue) {
    	boolean result=false;
        for (WaitingDialog dial : p_queue) {
            if (dial.client == null) {
            	if (dial.sentence != null) {
            		if (dial.console) {
            			ClientEngineZildo.guiDisplay.displayMessage(dial.sentence);
            		} else {
            			launchDialog(dial.sentence);
            		}
            	} else {
            		result=actOnDialog(dial.action);
            	}
            }
        }
        return result;
    }
	
	/**
	 * Ask GUI to display the current sentence.
	 * @param p_sentence
	 */
	public void launchDialog(String p_sentence) {
		
		currentSentence=p_sentence;
		ClientEngineZildo.guiDisplay.setText(currentSentence, GUIDisplay.DIALOGMODE_CLASSIC);
		ClientEngineZildo.guiDisplay.setToDisplay_dialoguing(true);
		positionInSentence=0;
		dialoguing=true;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// launchTopicSelection
	///////////////////////////////////////////////////////////////////////////////////////
	public void launchTopicSelection() {
		String currentSentence="La disparition\nLe mauvais temps\nLa dispute entre Henri et Lisa\nLa revolte des paysans\nLe prix des chaussures";
		ClientEngineZildo.guiDisplay.setText(currentSentence, GUIDisplay.DIALOGMODE_TOPIC);
		positionInSentence=0;
		selectedTopic=0;
		nProposedTopics=5;
	
		topicChoosing=true;
		dialoguing=true;
	}
	
	public void clearDialogs() {
		positionInSentence=-1;
		numToScroll=0;
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
		GUIDisplay guiManagement=ClientEngineZildo.guiDisplay;
	
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
				ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexte);
			}
			guiManagement.displayTextParts(positionInSentence,currentSentence,(numToScroll!=0));
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// manageTopic
	///////////////////////////////////////////////////////////////////////////////////////
	void manageTopic() {
		GUIDisplay guiManagement=ClientEngineZildo.guiDisplay;
		guiManagement.displayTopics(selectedTopic);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// actOnDialog
	///////////////////////////////////////////////////////////////////////////////////////
	// -We came here when player clicks ACTION, UP or DOWN
	// .Quit dialog
	// .Move on dialog
	// .Choose topic
	// -Returns TRUE if dialog is finished
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean actOnDialog(int actionDialog) {
		GUIDisplay guiManagement = ClientEngineZildo.guiDisplay;
		boolean entireMessageDisplay=guiManagement.isEntireMessageDisplay();
		boolean visibleMessageDisplay=guiManagement.isVisibleMessageDisplay();
	
		boolean result=false;
		
		if (topicChoosing) {
			// Topic
			switch (actionDialog) {
			case ACTIONDIALOG_ACTION:
				guiManagement.setToRemove_dialoguing(true);
				topicChoosing=false;
				result=true;
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
					result=true;
				}
			}
		}
		return result;
	}

}
