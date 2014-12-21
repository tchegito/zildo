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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.SpriteDisplay;
import zildo.client.gui.menu.HallOfFameMenu;
import zildo.client.sound.BankSound;
import zildo.fwk.FilterCommand;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.ui.EditableItemMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.fwk.ui.UnselectableItemMenu;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.items.Item;
import zildo.monde.items.ItemKind;
import zildo.monde.sprites.Reverse;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.FontDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.monde.util.Zone;
import zildo.resource.Constantes;
import zildo.server.MultiplayerManagement;
import zildo.server.state.PlayerState;

// Here we draw the Graphic User Interface. It means we paint screen in last moment,
// after all engines : Tile and Sprite.
// We draw:
// -frame with text inside
// -life, money
// -inventory
// -extra informations, various animations ...

// Other class communicate with this one by events:
// -toDisplay_dialoguing
// -toRemove_dialoguing

public class GUIDisplay {

	public enum DialogMode {
		CLASSIC, MENU, CREDITS, HALLOFFAME, INFO, BUY;
		
		public boolean isScript() {
			return true; //this == CLASSIC || this == CREDITS;
		}
		
		public boolean isBig() {
			return this == MENU || this == INFO || this == BUY;
		}
		
		public boolean isMenu() {
			return this == MENU || this == HALLOFFAME;
		}
	}
	
	// Alpha channel for virtual pad (specific Android) : range is 0..255
	final static int alphaPad = 128;
	
	// External variables for interacting with GUI
	private boolean toDisplay_dialoguing;
	private boolean toRemove_dialoguing;
	private boolean toDisplay_generalGui;
	private boolean toDisplay_scores;

	private DialogMode toDisplay_dialogMode;

	// External flags for text display

	DialogContext dialogContext;
	DialogDisplay dialogDisplay;
	
	private int countMoney;

	private GUISpriteSequence textDialogSequence; // All fonts displayed in dialog
	private GUISpriteSequence textMenuSequence; // All fonts displayed in menu
	private GUISpriteSequence frameDialogSequence; // Yellow frame for display dialog
	private GUISpriteSequence guiSpritesSequence; // All sprites designing the GUI
	private GUISpriteSequence menuSequence; // Cursors for menu
	private GUISpriteSequence creditSequence; // Credits
	private GUISpriteSequence infoSequence; // For displaying infos

	private ScreenConstant sc;
	
	// Menu items location (for Android)
	private Map<ItemMenu, Zone> itemsOnScreen = new HashMap<ItemMenu, Zone>();
	
	private FilterCommand filterCommand;
	private int arrowSprite;
	private float alpha;
	
	Stack<GameMessage> messageQueue;

	final int padSizeX;
	final int buttonSizeX;
	
	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public GUIDisplay() {
		toDisplay_dialoguing = false;
		toDisplay_generalGui = false;
		toRemove_dialoguing = false;

		// Initialize screen filter
		filterCommand = ClientEngineZildo.filterCommand;

		textDialogSequence = new GUISpriteSequence();
		textMenuSequence = new GUISpriteSequence();
		frameDialogSequence = new GUISpriteSequence();
		guiSpritesSequence = new GUISpriteSequence();
		menuSequence = new GUISpriteSequence();
		creditSequence = new GUISpriteSequence();
		infoSequence = new GUISpriteSequence();
		
		countMoney = 0;

		messageQueue = new Stack<GameMessage>();
		
		initTransco();
		
		// Screen constants
		sc = ClientEngineZildo.screenConstant;
		
		dialogContext = new DialogContext();
		dialogDisplay = new DialogDisplay(dialogContext, arrowSprite);
		
		// Get icon size, to handle left handed symetry
		SpriteBank fntBank = ClientEngineZildo.spriteDisplay.getSpriteBank(SpriteBank.BANK_FONTES);
		padSizeX = fntBank.get_sprite(FontDescription.VIRTUAL_PAD.getNSpr()).getTaille_x();
		buttonSizeX = fntBank.get_sprite(FontDescription.BUTTON_X.getNSpr()).getTaille_x();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// getIndexCharacter
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN :character in text sequence
	// OUT:given character's font position in the FONTES.PNJ sprite bank
	// /////////////////////////////////////////////////////////////////////////////////////
	public static final String transcoChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "-.,<>!?()'#$ÁÈÍË‡˚Ó‚Ù¸ˆ‰ÔÎ˘" + "abcdefghijklmnopqrstuvwxyz"
			+ "0123456789~£ß/:%";
	final Map<Character, Integer> mapTranscoChar = new HashMap<Character, Integer>();

	public static final int[] scriptLegibility = new int[transcoChar.length() + 6];
	public static final int[] scriptBigLegibility = new int[transcoChar.length()];
	
	private void initTransco() {
		for (int i = 0; i < transcoChar.length(); i++) {
			mapTranscoChar.put(transcoChar.charAt(i), i);
		}
		// Extra characters
		mapTranscoChar.put('^', FontDescription.GUI_BLUEDROP.getNSpr() - transcoChar.length());
		mapTranscoChar.put('§', FontDescription.GUI_RUPEE.getNSpr() - transcoChar.length());
		
		mapTranscoChar.put(' ', -1);
		
		// Legibility
		for (int i=0;i<scriptLegibility.length;i++) {
			scriptLegibility[i] = 0;
		}
		scriptLegibility[transcoChar.indexOf("L")] = 5;
		scriptLegibility[transcoChar.indexOf("R")] = 5;
		scriptLegibility[transcoChar.indexOf("M")] = 3;
		scriptLegibility[transcoChar.indexOf("Q")] = 3;
		scriptLegibility[transcoChar.indexOf("N")] = 3;
		scriptLegibility[transcoChar.indexOf("K")] = 7;
		scriptLegibility[transcoChar.indexOf("d")] = 1;
		scriptLegibility[transcoChar.indexOf("l")] = 1;
		scriptLegibility[transcoChar.indexOf("A")] = 1;
		for (int i=0;i<scriptBigLegibility.length;i++) {
			scriptBigLegibility[i] = scriptLegibility[i] * 2;
		}
		scriptBigLegibility[transcoChar.indexOf("Q")]--;
		scriptBigLegibility[transcoChar.indexOf("T")] = 5;
		scriptBigLegibility[transcoChar.indexOf("A")] = 4;
		scriptBigLegibility[transcoChar.indexOf("H")] = 1;
		scriptBigLegibility[transcoChar.indexOf("N")] = 8;
		scriptBigLegibility[transcoChar.indexOf("P")] = 1;
		scriptBigLegibility[transcoChar.indexOf("J")] = 1;
		scriptBigLegibility[transcoChar.indexOf("S")] = 1;
		scriptBigLegibility[transcoChar.indexOf("p")] = 1;
		scriptBigLegibility[transcoChar.indexOf("o")] = 1;
		scriptBigLegibility[transcoChar.indexOf("s")] = 1;
		scriptBigLegibility[transcoChar.indexOf("i")] = 1;
		scriptBigLegibility[transcoChar.indexOf("u")] = 1;
		scriptBigLegibility[transcoChar.indexOf("r")] = 1;
		scriptBigLegibility[transcoChar.indexOf("b")] = 1;
		scriptBigLegibility[transcoChar.indexOf("e")] = 1;
		scriptBigLegibility[transcoChar.indexOf("m")] = 1;
		scriptBigLegibility[transcoChar.indexOf("n")] = 1;
		scriptBigLegibility[transcoChar.indexOf("f")] = 1;
		scriptBigLegibility[transcoChar.indexOf("h")] = 1;
		scriptBigLegibility[transcoChar.indexOf("x")] = 1;
		
		arrowSprite = transcoChar.length() + mapTranscoChar.get('~');
	}

	int getIndexCharacter(char a) {
		Integer c = mapTranscoChar.get(a);
		if (c == null) {
			return 0;	// 'A' instead of NullPointer !
		}
		return c.intValue();
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// draw
	// /////////////////////////////////////////////////////////////////////////////////////
	// Main method for this class.
	// Should handle all events happening here.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void draw(boolean isMenu) {
		if (!isMenu) {
			// Re-initialize the gui's sprites sequence.
			// Each frame, we re-add the sprites to avoid doing test about what
			// exactly changes
			// from last frame.
			clean();
	
			if (toDisplay_generalGui) {
				// Draw the general GUI (life, money...)
				drawGeneralGUI();
			}
		}
		
		if (toRemove_dialoguing) {
			// Remove frame and text inside it
			removePreviousTextInFrame();
			removeFrame();
			toRemove_dialoguing = false;
			toDisplay_dialoguing = false;
		} else if (toDisplay_dialoguing) {
			// Draw frame and text inside it
			drawFrame();
		}

		if (toDisplay_scores) {
			drawScores();
		}

		drawConsoleMessages();
	}

	public void drawConsoleMessages() {
		int y = 230;
		List<GameMessage> toRemove = new ArrayList<GameMessage>();
		for (GameMessage mess : messageQueue) {
			ClientEngineZildo.ortho.drawText(23, y, mess.text, new Vector3f(
					1.0f, 1.0f, 1.0f));
			if (mess.duration-- == 0) {
				toRemove.add(mess);
			}
			y -= 8;
		}
		for (GameMessage mess : toRemove) {
			messageQueue.remove(mess);
		}
	}

	/**
	 * Determine text to display in the frame on the bottom of the screen and spawn fonts 
	 * corresponding to the given text.
	 */
	public void setText(String texte, DialogMode dialogMode) {
		toDisplay_dialogMode = dialogMode;
		removePreviousTextInFrame();
		prepareTextInFrame(texte, sc.TEXTER_COORDINATE_X,
				sc.TEXTER_COORDINATE_Y, false);
	}

	/**
	 * Build a sequence of characters corresponding to text format inside the
	 * text frame.<ul>
	 * <li>Use '-1' as 'SPACE'</li>
	 * <li>'-2' as 'ENDOFLINE'</li></ul>
	 * @param texte text to display
	 * @param p_posX start X position
	 * @param p_posY start Y postiion
	 * @return zone containing the calculated text
	 */ 
	public Zone prepareTextInFrame(String texte, int p_posX, int p_posY, boolean fullWidth) {
		// 1) Split sequence into list of words and measure size of text to render
		int length = texte.length() + 10;
		int[] nSpr = new int[length];
		SpriteModel spr = null;
		int nLettre = 0;
		int nLigne = 0;
		int sizeCurrentWord = 0;
		int sizeCurrentLine = 0;
		int lastSpacePosition = -1;
		int[] sizesLine = new int[Constantes.TEXTER_NUMLINE * 3];	// Total rows (with scroll)

		// Interpret dialog Mode
		int nBank;
		int sizeLine;
		// int nMaxLigne;
		boolean visibleFont;
		boolean center;
		int i;
		GUISpriteSequence seq = textDialogSequence; // Default sequence to add fonts

		int width = fullWidth ? Zildo.viewPortX : sc.TEXTER_SIZEX-4;
		nBank = SpriteBank.BANK_FONTES;
		sizeLine = sc.TEXTER_SIZELINE;
		int offsetNSpr = 0;
		if (!toDisplay_dialogMode.isBig()) {
			offsetNSpr =transcoChar.length();
			sizeLine = sc.TEXTER_SIZELINE_SCRIPT;
		}
		EngineFX fx = EngineFX.FOCUSED;
		
		switch (toDisplay_dialogMode) {
		case CLASSIC:
		default:
			visibleFont = false;
			center = false;
			break;
		case MENU:
		case HALLOFFAME:
			seq = textMenuSequence;
		case BUY:
			visibleFont = true;
			center = true;
			break;
		case CREDITS:
			center = true;
			visibleFont = true;
			seq = creditSequence;
			fx = EngineFX.NO_EFFECT;
			break;
		case INFO:
			center = true;
			visibleFont = false;
			seq = infoSequence;
			break;
		}

		for (i = 0; i <= texte.length(); i++) {
			char a;
			boolean signAlone = false; // Detect if a punctuation sign is alone (? or !)
			if (i == texte.length()) {
				a = 0;
			} else {
				a = texte.charAt(i);
				if (a == ' ' && i + 1 != texte.length()) {
					char b = texte.charAt(i + 1);
					signAlone = (b == '!' || b == '?');
				}
			}
			if (a == ' ' || a == 0 || (a == '#' && toDisplay_dialogMode != DialogMode.CREDITS) || a == '\n') {
				if (sizeCurrentLine + sizeCurrentWord > width
						|| a == '\n') {
					// We must cut the line before the current word
					if (a == '\n') {
						sizesLine[nLigne] = sizeCurrentLine + sizeCurrentWord;
						sizeCurrentWord = 0;
						nSpr[nLettre] = -2;
					} else {
						sizesLine[nLigne] = sizeCurrentLine;
						if (lastSpacePosition != -1) { // Put 'ENDOFLINE' at the last space
							nSpr[lastSpacePosition] = -2;
						} else { // No space from the beginning of the message
							nSpr[nLettre] = -2;
						}
					}
					dialogContext.add(i);
					nLigne++;
					sizeCurrentLine = 0;
				}
				if (a == ' ') {
					sizeCurrentLine += sc.TEXTER_SIZESPACE; // Space size
					nSpr[nLettre] = -1;
					if (!signAlone) {
						lastSpacePosition = nLettre;
					}
				} else if (a != '\n') { // 'ENDOFTEXT'
					break;
				}
				if (a != ' ' || !signAlone) {
					sizeCurrentLine += sizeCurrentWord;
					sizeCurrentWord = 0;
				}
			} else { // Regular character
				// Store sprite's index to display for this letter
				nSpr[nLettre] = getIndexCharacter(a);
				// Get sprite model to obtain horizontal size
				spr = ClientEngineZildo.spriteDisplay.getSpriteBank(nBank)
						.get_sprite(nSpr[nLettre] + offsetNSpr);
				sizeCurrentWord += (spr.getTaille_x() + 1);
				
				if (toDisplay_dialogMode.isScript()) { // && i < nLettre && texte.charAt(i+1) != ' ') {
					sizeCurrentWord--;
					sizeCurrentWord-= getLegibility(nSpr[nLettre]);
				}
			}
			nLettre++;
		}
		sizesLine[nLigne] = sizeCurrentLine + sizeCurrentWord;

		// 2) Display prepared sprites
		int x = p_posX;
		int y = p_posY;
		int offsetX = 0;
		int offsetY = 0;
		if (center) {
			offsetX = (sc.TEXTER_SIZEX - sizesLine[0]) / 2;
		}
		if (toDisplay_dialogMode.isMenu()) {
			offsetY-= (sc.TEXTER_SIZELINE * (nLigne+0)) ;
		}
		nLigne = 0;
		SpriteEntity lettre;
		Zone filledZone = new Zone();
		filledZone.x1 = offsetX;
		filledZone.y1 = y + offsetY;
		for (i = 0; i < nLettre; i++) {
			int indexSpr = nSpr[i];
			if (indexSpr == -1) {
				// Space
				offsetX += sc.TEXTER_SIZESPACE;
			} else if (indexSpr == -2) {
				offsetX = 0;
				offsetY += sizeLine;
				nLigne++;
				if (center) {
					offsetX = (sc.TEXTER_SIZEX - sizesLine[nLigne]) / 2;
				}
			} else {
				// Store font's pointer to easily remove it later and scroll
				// into the frame
				lettre = seq.addSprite(nBank, indexSpr + offsetNSpr, 
						x + offsetX, y + offsetY, 
						visibleFont, 255);
				lettre.setSpecialEffect(fx);
				spr = lettre.getSprModel();
				offsetX += (spr.getTaille_x() + 1);
				
				if (toDisplay_dialogMode.isScript()) {// && i < nLettre && nSpr[i+1] >= 0) { 
					// Special fonts with legibility (and no space after)
					offsetX--;
					offsetX-= getLegibility(indexSpr);
				}
			}
		}
		filledZone.x2 = x + offsetX - filledZone.x1;
		filledZone.y2 = y + offsetY - filledZone.y1;
		if (spr != null) {
			filledZone.y2 += spr.getTaille_y();
		}

		// Say that the message is not complete yet at screen
		if (toDisplay_dialogMode == DialogMode.CLASSIC) {
			dialogContext.visibleMessageDisplay = visibleFont; 
												
			dialogContext.entireMessageDisplay = visibleFont;
		}
		
		return filledZone;
	}

	private int getLegibility(int indexSpr) {
		if (toDisplay_dialogMode.isBig()) {
			return scriptBigLegibility[indexSpr];
		} else {
			return scriptLegibility[indexSpr];
		}
	}
	
	/**
	 * Remove current text. It can be Dialog or Menu
	 */
	void removePreviousTextInFrame() {
		GUISpriteSequence seq = textDialogSequence;
		if (toDisplay_dialogMode.isMenu()) {
			seq = textMenuSequence;
		}
		seq.clear();
	}

	/**
	 * Remove the frame's corner
	 */
	void removeFrame() {
		frameDialogSequence.clear();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// drawFrame
	// /////////////////////////////////////////////////////////////////////////////////////
	// Draw frame around displayed text
	// /////////////////////////////////////////////////////////////////////////////////////
	private final int couleur_cadre[] = {48, 149, 150, 149, 48};
	//private final int couleur_cadre[] = {48, 239, 238, 239, 48}; //{ 3, 203, 204, 203, 3 };

	void drawFrame() {
		int sizeX = sc.TEXTER_SIZEX;
		int sizeY = sc.TEXTER_SIZELINE_SCRIPT * sc.TEXTER_NUMLINE + 2;
		int posX1 = sc.TEXTER_COORDINATE_X - 10;
		int posX2 = sc.TEXTER_COORDINATE_X + sizeX;
		int posY1 = sc.TEXTER_COORDINATE_Y - 10;
		int posY2 = sc.TEXTER_COORDINATE_Y + sizeY;
		
		// Draw corner frame
		if (!frameDialogSequence.isDrawn()) {
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_LEFT, posX1 - 3, posY1 - 3);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_RIGHT, posX2, posY1 - 3);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_RIGHT, posX1 - 3, posY2 - 4, Reverse.ALL);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_LEFT, posX2 + 0, posY2 - 4, Reverse.ALL); 
			
			
			frameDialogSequence.addSprite(SpriteBank.BANK_FONTES, arrowSprite, 
					0,300,	// No matters coordinates, they will be update by dialogDisplay#animateArrow 
					false, 255);
		} else {
			// Animate arrow indicating next dialog, if necessary
			SpriteEntity entity = frameDialogSequence.get(4);
			dialogDisplay.animateArrow(entity);
		}

		// Draw frame's bars
		ClientEngineZildo.ortho.initDrawBox(false);
		for (int i = 0; i < 5; i++) {
			ClientEngineZildo.ortho.boxOpti(posX1+7, posY1 + i, sizeX+4, 1,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(posX1+6, posY2+6 - i, sizeX+4, 1,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(posX1 + i, posY1+7, 1, sizeY+3,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(posX2+6 - i, posY1+7, 1, sizeY+3,
					couleur_cadre[i], null);
		}
		ClientEngineZildo.ortho.endDraw();
		ClientEngineZildo.ortho.enableBlend();
		ClientEngineZildo.ortho.box(posX1+5, posY1+5, sizeX+7, sizeY+7, 0, new Vector4f(0.4f, 0.2f, 0.1f, 0.7f));
		ClientEngineZildo.ortho.disableBlend();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// displayTextParts
	// /////////////////////////////////////////////////////////////////////////////////////
	public void displayTextParts(boolean scrolling) {
		Iterator<SpriteEntity> it = textDialogSequence.iterator();
		SpriteEntity entity = null;
		char a = ' ';
		
		// We have to know how much font have to be enabled/disabled (with
		// visibility)
		for (int i = 0; i < dialogContext.pos + 1; i++) {
			
			if (i < dialogContext.sentence.length()) {
				a = dialogContext.sentence.charAt(i);
			}
			
			if (a != ' ' && it.hasNext()) { // || i == dialogContext.sentence.length()) {
				entity = it.next();
			}
				if (entity.getScrY() < sc.TEXTER_COORDINATE_Y) {
					entity.setVisible(false);
				} else if (entity.getScrY() < (sc.TEXTER_BOTTOM_Y)) {
					entity.setVisible(true);
					if (i == dialogContext.sentence.length() && !dialogContext.entireMessageDisplay) {
						dialogContext.entireMessageDisplay = true;
						dialogDisplay.displayArrow(1);
						//ClientEngineZildo.soundPlay
						//		.playSoundFX(BankSound.AfficheTexteFin);
					}
				} else if (!dialogContext.visibleMessageDisplay) {
					dialogContext.visibleMessageDisplay = true;
					dialogDisplay.displayArrow(2);
					// If the text has another line to scroll, don't play sound
					if (!scrolling) {
						//ClientEngineZildo.soundPlay
						//		.playSoundFX(BankSound.AfficheTexteFin);
					}
				}
		}
	}

	public void skipDialog(String sentence) {
		boolean entire = true;
		for (SpriteEntity entity : textDialogSequence) {
			int y = entity.getScrY();
			if (y < sc.TEXTER_COORDINATE_Y) {
				entity.setVisible(false);
			} else if (y >= sc.TEXTER_COORDINATE_Y && y < sc.TEXTER_BOTTOM_Y) {
				entity.setVisible(true);
			} else if (y > sc.TEXTER_BOTTOM_Y) {
				entire = false;	// There's still some text to display
			}
		}
		if (!entire) {
			dialogContext.setLine(sc.TEXTER_NUMLINE);
			dialogDisplay.displayArrow(2);
		} else {
			dialogDisplay.displayArrow(1);
		}
		dialogContext.visibleMessageDisplay = true;
		dialogContext.entireMessageDisplay = entire;
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// scrollAndDisplayTextParts
	// /////////////////////////////////////////////////////////////////////////////////////
	// Scroll every fonts one row to the height
	// And display fonts which are inside the frame
	// /////////////////////////////////////////////////////////////////////////////////////
	public void scrollAndDisplayTextParts() {
		for (SpriteEntity entity : textDialogSequence) {
			entity.setScrY(entity.getScrY() - sc.TEXTER_SIZELINE);
		}

		displayTextParts(false);
		dialogContext.visibleMessageDisplay = false;
	}

	/**
	 * Display scrolling credits
	 * @param counter position in the sequence of lines
	 * @param nextSentence incoming sentence
	 */
	public void displayCredits(int counter, String nextSentence) {
		setToDisplay_dialogMode(DialogMode.CREDITS);

		// All moves upward
		List<SpriteEntity> toRemove = new ArrayList<SpriteEntity>();
		for (SpriteEntity entity : creditSequence) {
			int y = entity.getScrY();
			if (y < -16) {
				toRemove.add(entity);
			}
			entity.setScrY(y -1);
		}
		for (SpriteEntity ent : toRemove) {
			creditSequence.remove(ent);
			ClientEngineZildo.spriteDisplay.deleteSprite(ent);
		}

		// New line
		if (counter % 16 == 0) {
			int y = Zildo.viewPortY; //counter >> 4; // divided by 16
			prepareTextInFrame(nextSentence, sc.TEXTER_COORDINATE_X, y, true);
		}
		
	}
	
	public void displayInfo(int y, String nextSentence, EntityTransformer action) {
		setToDisplay_dialogMode(DialogMode.INFO);	
		
		if (nextSentence != null) {
			prepareTextInFrame(nextSentence, sc.TEXTER_COORDINATE_X, y, true);
		}
		for (SpriteEntity ent : infoSequence) {
			action.transform(ent);
		}
	}
	/**
	 * Display a menu
	 * 
	 * @param p_menu
	 *            (can't be null)
	 */
	public void displayMenu(Menu p_menu) {
		int sizeY = (p_menu.items.size() + 2) * sc.TEXTER_MENU_SIZEY;
		int startY = (Zildo.viewPortY - sizeY) / 2;
		if (!p_menu.displayed) {
			// Display menu's text
			if (p_menu instanceof HallOfFameMenu) {
				setToDisplay_dialogMode(DialogMode.HALLOFFAME);
			} else {
				setToDisplay_dialogMode(DialogMode.MENU);
			}
			int posY = startY;
			removePreviousTextInFrame();
			// Title
			prepareTextInFrame(p_menu.title, sc.TEXTER_COORDINATE_X,
					posY, false);
			posY += 2 * sc.TEXTER_MENU_SIZEY;
			
			// Items
			itemsOnScreen.clear();
			for (ItemMenu item : p_menu.items) {
				boolean unselectable = item instanceof UnselectableItemMenu;
				Zone z = prepareTextInFrame(item.getText(),
						sc.TEXTER_COORDINATE_X, posY, unselectable);
				posY += sc.TEXTER_MENU_SIZEY;
				if (item instanceof EditableItemMenu) {
					z.x1 = sc.TEXTER_COORDINATE_X;
					z.x2 = sc.TEXTER_SIZEX;
					z.y2 = sc.TEXTER_SIZELINE;
				}
				// Store item location
				itemsOnScreen.put(item, z);
			}
			p_menu.displayed = true;
		}
		menuSequence.clear();
		int y = startY + (p_menu.selected + 2) * sc.TEXTER_MENU_SIZEY;
		alpha += 0.1f;
		int wave = (int) (10.0f * Math.sin(alpha));
		menuSequence.addSprite(FontDescription.GUI_BLUEDROP, 40 + wave, y + 5);
		menuSequence.addSprite(FontDescription.GUI_BLUEDROP, Zildo.viewPortX
				- 40 - wave, y + 5, Reverse.HORIZONTAL);
	}

	public void endMenu() {
		menuSequence.clear();
		removePreviousTextInFrame();
		// Put back in default mode
		toDisplay_dialogMode = DialogMode.CLASSIC;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// fadeIn
	// /////////////////////////////////////////////////////////////////////////////////////
	public void fadeIn(FilterEffect... p_effects) {
		filterCommand.fadeIn(p_effects);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// fadeOut
	// /////////////////////////////////////////////////////////////////////////////////////
	public void fadeOut(FilterEffect... p_effects) {
		filterCommand.fadeOut(p_effects);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// isFadeOver
	// /////////////////////////////////////////////////////////////////////////////////////
	public boolean isFadeOver() {
		return filterCommand.isFadeOver();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// drawGeneralGUI
	// /////////////////////////////////////////////////////////////////////////////////////
	void drawGeneralGUI() {
		SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
		PersoPlayer zildo = (PersoPlayer) spriteDisplay.getZildo();
		if (zildo == null) {
			return;
		}

		final int GUI_Y = 4; //35;
		final int WEAPON_X = 8;
		final int DROPS_X = 300; //248;

		int i;
		// Life
		//guiSpritesSequence.addSprite(lifeGui, 207, 10);
		if (zildo.hasItem(ItemKind.NECKLACE)) {
			for (i = 0; i < (zildo.getMaxpv()) / 2; i++) {
				int pv = zildo.getPv();
				FontDescription desc;
				if (i == pv >> 1 && pv % 2 == 1) {
					desc = FontDescription.GUI_HALFBLUEDROP; // Half drop
				} else if (pv >> 1 <= i) {
					desc = FontDescription.GUI_EMPTYBLUEDROP; // Empty drop
				} else {
					desc = FontDescription.GUI_BLUEDROP; // Full drop
				}
				guiSpritesSequence.addSprite(desc, DROPS_X - ((i - 1) % 10) * 8,
						GUI_Y); //20 + 8 * ((i - 1) / 10));
			}
		}

		// Money
		if (zildo.who.canInventory) {
			guiSpritesSequence.addSprite(FontDescription.GUI_RUPEE, 97, GUI_Y);
			if (countMoney != zildo.getMoney()) {
				if (countMoney < zildo.getMoney()) {
					countMoney++;
				} else {
					countMoney--;
				}
				if (zildo.getMoney() - countMoney % 20 == 0) {
					ClientEngineZildo.soundPlay
							.playSoundFX(BankSound.ZildoGagneArgent);
				}
			}
			displayNumber(countMoney, 4, 87, GUI_Y);
	
			// Bombs
			if (zildo.hasItem(ItemKind.DYNAMITE)) {
				guiSpritesSequence.addSprite(FontDescription.GUI_BOMB, 136, GUI_Y - 2);
				displayNumber(zildo.getCountBomb(), 2, 126, GUI_Y);
			}
	
			// Arrows
			if (zildo.hasItem(ItemKind.BOW)) {
				guiSpritesSequence.addSprite(FontDescription.GUI_ARROW, 174, GUI_Y);
				displayNumber(zildo.getCountArrow(), 2, 164, GUI_Y);
			}
			
			// Keys
			if (zildo.getCountKey() > 0) {
				guiSpritesSequence.addSprite(FontDescription.GUI_KEY, 211, GUI_Y + 2);
				displayNumber(zildo.getCountKey(), 1, 201, GUI_Y);
			}
			
			// Current weapon
			guiSpritesSequence.addSprite(FontDescription.GUI_WEAPONFRAME, WEAPON_X, 0, Reverse.NOTHING, alphaPad);
			guiSpritesSequence.addSprite(FontDescription.GUI_WEAPONFRAME, WEAPON_X+16, 0, Reverse.HORIZONTAL, alphaPad);
			guiSpritesSequence.addSprite(FontDescription.GUI_WEAPONFRAME, WEAPON_X+0, 15, Reverse.VERTICAL, alphaPad);
			guiSpritesSequence.addSprite(FontDescription.GUI_WEAPONFRAME, WEAPON_X+16, 15, Reverse.ALL, alphaPad);
			Item weapon = zildo.getWeapon();
			if (weapon != null) {
				SpriteDescription desc = weapon.kind.representation;
				SpriteModel spr = ClientEngineZildo.spriteDisplay.getSpriteBank(desc.getBank())
						.get_sprite(desc.getNSpr());
				int sx = spr.getTaille_x();
				int sy = spr.getTaille_y();
				guiSpritesSequence.addSprite(desc, WEAPON_X + 11 + 4-(sx >> 1), GUI_Y + 8 + 4 -(sy >> 1));
				
				if (weapon.kind == ItemKind.NECKLACE) {
					// Display number of moon fragments
					int nQuarter = zildo.getMoonHalf();
					if (nQuarter > 0 && nQuarter < 10) {
						displayNumber(nQuarter, 1, WEAPON_X + 11 + 6, GUI_Y + 8 + 6);
					}
				}
			}
		}
		// virtual pad
		if (PlatformDependentPlugin.currentPlugin == PlatformDependentPlugin.KnownPlugin.Android) {
			int curAlpha = alphaPad;
			if (dialogDisplay.isDialoguing()) {
				curAlpha>>=2;
			}
			Point crossCenter = null;
			boolean movingCross = ClientEngineZildo.client.isMovingCross();
			if (movingCross) {
				crossCenter = ClientEngineZildo.client.getCrossCenter();
			} else {
				crossCenter = new Point(10 + (80/2), Zildo.viewPortY - (80/2));
			}
			if (crossCenter != null) {
				int x1 = computeForLeftHanded(crossCenter.x - (80/2), FontDescription.VIRTUAL_PAD);
				guiSpritesSequence.addSprite(FontDescription.VIRTUAL_PAD, x1, crossCenter.y -(80/2), curAlpha);
			}
			int x2 = computeForLeftHanded(Zildo.viewPortX - 24 - 16 + 2, FontDescription.BUTTON_Y);
			int x3 = computeForLeftHanded(Zildo.viewPortX - 48 - 16 - 1, FontDescription.BUTTON_X);
			guiSpritesSequence.addSprite(FontDescription.BUTTON_Y, x2, Zildo.viewPortY-70 - 3, curAlpha);
			guiSpritesSequence.addSprite(FontDescription.BUTTON_X, x3, Zildo.viewPortY-40 + 8, curAlpha);
		}
	}

	private int computeForLeftHanded(int x, FontDescription desc) {
		if (ClientEngineZildo.client.isLeftHanded()) {
			switch (desc) {
			case VIRTUAL_PAD:
				return Zildo.viewPortX - x - padSizeX;
			case BUTTON_X:
			case BUTTON_Y:
				return Zildo.viewPortX - x - buttonSizeX;
			}
		}
		return x;
	}

	private void displayNumber(int p_number, int p_numDigit, int p_x, int p_y) {
		int lastPos = p_x + 2;
		for (int i = 0; i < p_numDigit; i++) {
			int j = p_number;
			if (i == 2 && p_number >= 100) {
				j = j / 100;
			} else if (i == 1 && p_number >= 10) {
				j = j / 10;
			} else if (i > 0) {
				break;
			}
			j = j % 10;
			FontDescription desc = FontDescription.values()[FontDescription.N_0
					.ordinal() + j];

			guiSpritesSequence.addSprite(desc, lastPos - i * 7, p_y);
		}
	}

	public void displayMessage(String text) {
		messageQueue.add(0, new GameMessage(text));
	}
	
	public void clearMessages() {
		messageQueue.clear();
	}

	public void setupHero(PersoPlayer zildo) {
		if (zildo != null) {
			countMoney = zildo.getMoney();
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// clean
	// /////////////////////////////////////////////////////////////////////////////////////
	private void clean() {
		guiSpritesSequence.clear();
	}

	/**
	 * Reinit GUI when a game is finished, to be ready next time.
	 */
	public void clearGui() {
		setToDisplay_generalGui(false);
		setToDisplay_dialoguing(false);
		countMoney = 0;
		clearSequences(127);
	}
	
	public boolean isToDisplay_dialoguing() {
		return toDisplay_dialoguing;
	}

	public void setToDisplay_dialoguing(boolean toDisplay_dialoguing) {
		this.toDisplay_dialoguing = toDisplay_dialoguing;
	}

	public boolean isToRemove_dialoguing() {
		return toRemove_dialoguing;
	}

	public void setToRemove_dialoguing(boolean toRemove_dialoguing) {
		this.toRemove_dialoguing = toRemove_dialoguing;
	}

	public boolean isToDisplay_generalGui() {
		return toDisplay_generalGui;
	}

	public void setToDisplay_generalGui(boolean toDisplay_generalGui) {
		this.toDisplay_generalGui = toDisplay_generalGui;
	}

	public DialogMode getToDisplay_dialogMode() {
		return toDisplay_dialogMode;
	}

	public void setToDisplay_dialogMode(DialogMode toDisplay_dialogMode) {
		this.toDisplay_dialogMode = toDisplay_dialogMode;
	}

	public void setToDisplay_scores(boolean p_active) {
		this.toDisplay_scores = p_active;
	}

	/**
	 * Returns item on given location, if there's any. NULL otherwise.
	 * @param x
	 * @param y
	 * @return item (or NULL)
	 */
	public ItemMenu getItemOnLocation(int x, int y) {
		for (Entry<ItemMenu, Zone> e : itemsOnScreen.entrySet()) {
			if (e.getValue().isInto(x, y)) {
				return e.getKey();
			}
		}
		return null;
	}
	
	/**
	 * Draw the score panel.
	 */
	public void drawScores() {
		Collection<PlayerState> states = ClientEngineZildo.client
				.getPlayerStates();
		int sizeX = 6 * 20 + 6 * 10 + 6;
		int sizeY = 16 + states.size() * 6;
		int posX = (Zildo.viewPortX - sizeX) / 2;
		int posY = (Zildo.viewPortY - sizeY) / 2;
		int y = posY + 14;
		// Title
		ClientEngineZildo.ortho.drawText(-1, posY + 3, "scores", new Vector3f(
				1.0f, 1.0f, 1.0f));
		// Scores
		for (PlayerState state : states) {
			StringBuilder sb = new StringBuilder();
			int score = MultiplayerManagement.getScore(state);
			sb.append(state.playerName);
			String scoreStr = String.valueOf(score);
			int nSpace = 30 - sb.length() - scoreStr.length();
			for (int j = 0; j < nSpace; j++) {
				sb.append(" ");
			}
			sb.append(scoreStr);
			ClientEngineZildo.ortho.drawText(posX + 3, y, sb.toString(),
					new Vector3f(1.0f, 1.0f, 1.0f));
			y += 8;
		}
		// Draw a transparent box
		ClientEngineZildo.ortho.enableBlend();
		ClientEngineZildo.ortho.box(posX, posY, sizeX, sizeY, 0, new Vector4f(
				0.3f, 0.2f, 0.4f, 0.2f));
		ClientEngineZildo.ortho.disableBlend();
		ClientEngineZildo.ortho.boxv(posX, posY, sizeX, sizeY, 4, new Vector4f(
				0.9f, 0.7f, 0.4f, 0.4f));
		ClientEngineZildo.ortho.boxv(posX - 1, posY - 1, sizeX + 2, sizeY + 2,
				4, new Vector4f(0.8f, 0.6f, 0.4f, 0.4f));

	}
	
	public void manageDialog() {
		if (dialogDisplay.isDialoguing()) {
			dialogDisplay.manageDialog();
		}		
	}
	
	public boolean launchDialog(List<WaitingDialog> p_queue) {
		return dialogDisplay.launchDialog(p_queue);
	}
	
	public void clearSequences(int p_which) {
		if ((p_which & 1) != 0) {
			textDialogSequence.clear();
			dialogDisplay.actOnDialog(null, CommandDialog.STOP);
		}
		if ((p_which & 2) != 0) {
			textMenuSequence.clear();
		}
		if ((p_which & 4) != 0) {
			frameDialogSequence.clear();
		}
		if ((p_which & 8) != 0) {
			guiSpritesSequence.clear();
		}
		if ((p_which & 16) != 0) {
			menuSequence.clear();
		}
		if ((p_which & 32) != 0) {
			creditSequence.clear();
		}
		if ((p_which & 64) != 0) {
			infoSequence.clear();
		}
	}
}