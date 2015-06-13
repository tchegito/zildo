/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.client.gui;

import java.util.List;

import zildo.client.ClientEngineZildo;
import zildo.client.ClientEventNature;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.client.sound.BankSound;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.server.EngineZildo;

public class DialogDisplay {
	
	public boolean dialoguing;
	public DialogContext context;
	private int beta;
	private int arrowDisplay;	// 0=no arrow / 1=horizontal / 2=vertical
	
	final int[] curve = {0,1,2,3,4,4,5,5,5};
	final int[] spr = { 0,0,0,1,1,1,1,2,2};
	final int arrowX;
	final int arrowY;
	final int arrowSprite;
	
	public DialogDisplay(DialogContext dialogContext, int sprite) {
		dialoguing = false;
		context = dialogContext;
		
		// Screen constants
		ScreenConstant sc = ClientEngineZildo.screenConstant;
		arrowX = sc.TEXTER_COORDINATE_X + sc.TEXTER_SIZEX - 12;
		arrowY = sc.TEXTER_COORDINATE_Y + sc.TEXTER_SIZELINE * 3 + 8 - 16;
		arrowSprite = sprite;
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
		if (dialoguing) {
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
            	if (dial.sentence != null && dial.action != CommandDialog.CONTINUE) {
            		if (dial.console) {
            			ClientEngineZildo.guiDisplay.displayMessage(dial.sentence);
            		} else {
            			launchDialog(dial.who, dial.sentence, dial.action);
            		}
            	} else {
            		result=actOnDialog(dial.sentence, dial.action);
            	}
            }
        }
        return result;
    }
	
	/**
	 * Ask GUI to display the current sentence.
	 * @param p_who TODO
	 * @param p_sentence
	 * @param p_dialAction optional
	 */
	public void launchDialog(String p_who, String p_sentence, CommandDialog p_dialAction) {
		DialogMode displayMode = DialogMode.CLASSIC;
		if (p_dialAction == CommandDialog.BUYING) {
			// Hero is looking items in a store : so display sentence centered and directly
			displayMode = DialogMode.BUY;
		}

		String txt = "";
		if (!ZUtils.isEmpty(p_who)) {
			txt += p_who+":" + (char) -3;
		}
		txt += p_sentence.replaceAll("[@|$]", "");
		context.who = p_who;
		context.setSentence(txt);
		ClientEngineZildo.guiDisplay.setText(context.sentence, displayMode);
		ClientEngineZildo.guiDisplay.setToDisplay_dialoguing(true);
		dialoguing=true;
		
		displayArrow(0);
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
		GUIDisplay guiDisplay=ClientEngineZildo.guiDisplay;
	
		if (context.entireMessageDisplay || context.visibleMessageDisplay) {
			if (context.numToScroll!=0) {
				context.numToScroll--;
				if (!context.entireMessageDisplay) {
					guiDisplay.scrollAndDisplayTextParts();
				}
			} else if (context.entireMessageDisplay && !context.fullSentenceDisplayed) {
			    // Tell server that sentence is full displayed
			    ClientEngineZildo.askEvent(ClientEventNature.DIALOG_FULLDISPLAY);
			    context.fullSentenceDisplayed = true;
			}
		} else if (!context.visibleMessageDisplay ) {
			// Draw sentences slowly (word are appearing one after another)
			if (context.sentence.length() > context.pos) {
				context.pos++;
			}
			/*
			if (context.pos % 3 ==0 && (Math.random()*10)>7) {
				ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexte);
			}
			*/
			guiDisplay.displayTextParts(context.numToScroll!=0);
		}
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
	public boolean actOnDialog(String p_sentence, CommandDialog actionDialog) {
		GUIDisplay guiDisplay = ClientEngineZildo.guiDisplay;
	
		boolean result=false;
		
		if (dialoguing) {
			// Conversation
			switch (actionDialog) {
				case ACTION:
					if (!context.visibleMessageDisplay && !context.entireMessageDisplay) {
						guiDisplay.skipDialog(p_sentence);
						break;
					}
				case CONTINUE:
					if (context.entireMessageDisplay || context.visibleMessageDisplay) {
						// Two cases : continue or quit
						if (!context.entireMessageDisplay) {
							context.numToScroll=3;
						} else {
							ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexteSuivant);
						    if (actionDialog == CommandDialog.CONTINUE) {
								launchDialog(context.who, p_sentence, actionDialog);
								return false;
						    } else {
								// Quit dialog
								guiDisplay.setToRemove_dialoguing(true);
								dialoguing=false;
								result=true;
						    }
						}
					}
					break;
				case STOP:
					guiDisplay.setToRemove_dialoguing(true);
					dialoguing=false;
					break;
			}
		} else {
			// Dialog is already over, so we inform the caller (this happens when player press ACTION and ATTACK at the same time
			// during a dialog).
			result = true;
		}
		return result;
	}

	public void displayArrow(int arr) {
		arrowDisplay = arr;
	}
	
	public void animateArrow(SpriteEntity entity) {
		if (arrowDisplay == 0) {
			entity.setVisible(false);
			return;
		}
		int shift = curve[(beta/4) % curve.length];
		int sprite = spr[(beta/4) % spr.length];
		entity.setNSpr(arrowSprite + sprite);
		entity.setSprModel(EngineZildo.spriteManagement.getSpriteBank(entity.getNBank())
				.get_sprite(entity.getNSpr()));
		if (arrowDisplay == 2) {	// Vertical
			entity.rotation = Rotation.CLOCKWISE;
			entity.setScrX(arrowX - 2);
			entity.setScrY(arrowY + shift);
		} else {	// Horizontal
			entity.rotation = Rotation.NOTHING;
			entity.setScrX(arrowX + shift);
			entity.setScrY(arrowY);
		}
		entity.setVisible(true);
		beta++;
	}
}
