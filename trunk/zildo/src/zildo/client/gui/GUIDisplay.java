/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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
import zildo.client.SpriteDisplay;
import zildo.client.sound.BankSound;
import zildo.fwk.FilterCommand;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.FontDescription;
import zildo.monde.sprites.persos.PersoZildo;
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
		CLASSIC, TOPIC, MENU;
	}
	
	// External variables for interacting with GUI
	private boolean toDisplay_dialoguing;
	private boolean toRemove_dialoguing;
	private boolean toDisplay_generalGui;
	private boolean toDisplay_scores;

	private DialogMode toDisplay_dialogMode;

	// External flags for text display
	private boolean visibleMessageDisplay; // FALSE=Visible text isn't display
											// yet at screen
	private boolean entireMessageDisplay; // FALSE=Entire sentence aren't
											// display yet at screen.

	private int countMoney;

	private GUISpriteSequence textDialogSequence; // All fonts displayed in
													// dialog
	private GUISpriteSequence textMenuSequence; // All fonts displayed in menu
	private GUISpriteSequence frameDialogSequence; // Yellow frame for display
													// dialog
	private GUISpriteSequence guiSpritesSequence; // All sprites designing the
													// GUI
	private GUISpriteSequence menuSequence; // Cursors for menu

	// Menu items location (for Android)
	private Map<ItemMenu, Zone> itemsOnScreen = new HashMap<ItemMenu, Zone>();
	
	private FilterCommand filterCommand;

	public float alpha;

	Stack<GameMessage> messageQueue;

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

		countMoney = 0;

		messageQueue = new Stack<GameMessage>();
		
		initTransco();
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

	private void initTransco() {
		for (int i = 0; i < transcoChar.length(); i++) {
			mapTranscoChar.put(transcoChar.charAt(i), i);
		}
		mapTranscoChar.put(' ', -1);
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
	public void draw() {
		// Re-initialize the gui's sprites sequence.
		// Each frame, we re-add the sprites to avoid doing test about what
		// exactly changes
		// from last frame.
		clean();

		if (toDisplay_generalGui) {
			// Draw the general GUI (life, money...)
			drawGeneralGUI();
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
			ClientEngineZildo.ortho.drawText(0, y, mess.text, new Vector3f(
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
		prepareTextInFrame(texte, Constantes.TEXTER_COORDINATE_X,
				Constantes.TEXTER_COORDINATE_Y);
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
	public Zone prepareTextInFrame(String texte, int p_posX, int p_posY) {
		// 1) Split sequence into list of words and measure size of text to render
		int length = texte.length() + 10;
		int[] nSpr = new int[length];
		SpriteModel spr = null;
		int nLettre = 0;
		int nLigne = 0;
		int sizeCurrentWord = 0;
		int sizeCurrentLine = 0;
		int lastSpacePosition = -1;
		int[] sizesLine = new int[Constantes.MAX_TOPICS];

		// Interpret dialog Mode
		int nBank;
		int sizeLine;
		// int nMaxLigne;
		boolean visibleFont;
		boolean center;
		int i;
		GUISpriteSequence seq = textDialogSequence; // Default sequence to add
													// fonts

		switch (toDisplay_dialogMode) {
		case CLASSIC:
		default:
			nBank = SpriteBank.BANK_FONTES;
			sizeLine = Constantes.TEXTER_SIZELINE;
			visibleFont = false;
			center = false;
			break;
		case MENU:
			nBank = SpriteBank.BANK_FONTES;
			sizeLine = Constantes.TEXTER_SIZELINE;
			visibleFont = true;
			center = true;
			seq = textMenuSequence;
			break;
		case TOPIC:
			nBank = SpriteBank.BANK_FONTES2;
			sizeLine = Constantes.TEXTER_TOPIC_SIZELINE;
			visibleFont = true;
			center = true;
			break;
		}

		for (i = 0; i <= texte.length(); i++) {
			char a;
			boolean signAlone = false; // Detect if a punctuation sign is alone
										// (? or !)
			if (i == texte.length()) {
				a = 0;
			} else {
				a = texte.charAt(i);
				if (a == ' ' && i + 1 != texte.length()) {
					char b = texte.charAt(i + 1);
					signAlone = (b == '!' || b == '?');
				}
			}
			if (a == ' ' || a == 0 || a == '#' || a == '\n') {
				if (sizeCurrentLine + sizeCurrentWord > Constantes.TEXTER_SIZEX
						|| a == '\n') {
					// We must cut the line before the current word
					if (a == '\n') {
						sizesLine[nLigne] = sizeCurrentLine + sizeCurrentWord;
						sizeCurrentWord = 0;
						nSpr[nLettre] = -2;
					} else {
						sizesLine[nLigne] = sizeCurrentLine;
						if (lastSpacePosition != -1) { // Put 'ENDOFLINE' at the
														// last space
							nSpr[lastSpacePosition] = -2;
						} else { // No space from the beginning of the message
							nSpr[nLettre] = -2;
						}
					}
					nLigne++;
					sizeCurrentLine = 0;
				}
				if (a == ' ') {
					sizeCurrentLine += Constantes.TEXTER_SIZESPACE; // Space
																	// size
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
						.get_sprite(nSpr[nLettre]);
				sizeCurrentWord += (spr.getTaille_x() + 1);
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
			offsetX += (Constantes.TEXTER_SIZEX - sizesLine[0]) / 2;
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
				offsetX += Constantes.TEXTER_SIZESPACE;
			} else if (indexSpr == -2) {
				offsetX = 0;
				offsetY += sizeLine;
				nLigne++;
				if (center) {
					offsetX += (Constantes.TEXTER_SIZEX - sizesLine[nLigne]) / 2;
				}
			} else {
				// Store font's pointer to easily remove it later and scroll
				// into the frame
				lettre = seq.addSprite(nBank, indexSpr, x + offsetX, y
						+ offsetY, visibleFont);
				spr = lettre.getSprModel();
				offsetX += (spr.getTaille_x() + 1);
			}
		}
		filledZone.x2 = x + offsetX - filledZone.x1;
		filledZone.y2 = y + offsetY - filledZone.y1;
		if (spr != null) {
			filledZone.y2 += spr.getTaille_y();
		}

		// Say that the message is not complete yet at screen
		visibleMessageDisplay = visibleFont; 
												
		entireMessageDisplay = visibleFont;
		
		return filledZone;
	}

	/**
	 * Remove current text. It can be Dialog or Menu
	 */
	void removePreviousTextInFrame() {
		GUISpriteSequence seq = textDialogSequence;
		if (toDisplay_dialogMode == DialogMode.MENU) {
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
	private final int couleur_cadre[] = { 3, 9, 9, 169, 3 };

	void drawFrame() {
		// Draw corner frame
		if (!frameDialogSequence.isDrawn()) {
			frameDialogSequence
					.addSprite(FontDescription.FRAME_UPLEFT, 40, 170);
			frameDialogSequence.addSprite(FontDescription.FRAME_UPRIGHT, 280,
					170);
			frameDialogSequence.addSprite(FontDescription.FRAME_DOWNLEFT, 40,
					230);
			frameDialogSequence.addSprite(FontDescription.FRAME_DOWNRIGHT, 280,
					230);
		}

		// Draw frame's bars
		for (int i = 0; i < 5; i++) {
			ClientEngineZildo.ortho.initDrawBox(false);
			ClientEngineZildo.ortho.boxOpti(47, 170 + i, 233, 1,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(47, 236 - i, 233, 1,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(40 + i, 177, 1, 53,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(286 - i, 177, 1, 53,
					couleur_cadre[i], null);
			ClientEngineZildo.ortho.endDraw();
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// displayTextParts
	// /////////////////////////////////////////////////////////////////////////////////////
	public void displayTextParts(int position, String sentence,
			boolean scrolling) {
		Iterator<SpriteEntity> it = textDialogSequence.iterator();
		int j = 0;
		char a = 0;

		// We have to know how much font have to be enabled/disabled (with
		// visibility)
		for (int i = 0; i < position + 1; i++) {
			if (i < sentence.length()) {
				a = sentence.charAt(i);
			}
			if (a == ' ' || i == sentence.length()) {
				for (int k = 0; k < (i - j) && it.hasNext(); k++) {
					SpriteEntity entity = it.next();
					if (entity.getScrY() < Constantes.TEXTER_COORDINATE_Y) {
						entity.setVisible(false);
					} else if (entity.getScrY() < (Constantes.TEXTER_COORDINATE_Y
							+ (Constantes.TEXTER_NUMLINE * Constantes.TEXTER_SIZELINE) - 10)) {
						entity.setVisible(true);
						if (i == sentence.length()) {
							entireMessageDisplay = true;
							ClientEngineZildo.soundPlay
									.playSoundFX(BankSound.AfficheTexteFin);
						}
					} else {
						visibleMessageDisplay = true;
						// If the text has another line to scroll, don't play
						// sound
						if (!scrolling) {
							ClientEngineZildo.soundPlay
									.playSoundFX(BankSound.AfficheTexteFin);
						}
					}
				}
				j = i + 1;
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// scrollAndDisplayTextParts
	// /////////////////////////////////////////////////////////////////////////////////////
	// Scroll every fonts one row to the height
	// And display fonts which are inside the frame
	// /////////////////////////////////////////////////////////////////////////////////////
	public void scrollAndDisplayTextParts(int position, String sentence) {
		for (SpriteEntity entity : textDialogSequence) {
			entity.setScrY(entity.getScrY() - Constantes.TEXTER_SIZELINE);
		}

		displayTextParts(position, sentence, false);
		visibleMessageDisplay = false;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// displayTopics
	// /////////////////////////////////////////////////////////////////////////////////////
	public void displayTopics(int selected) {
		for (SpriteEntity entity : textDialogSequence) {
			int numLigne = (entity.getScrY() - Constantes.TEXTER_COORDINATE_Y)
					/ Constantes.TEXTER_TOPIC_SIZELINE;
			if (numLigne == selected) {
				entity.setSpecialEffect(EngineFX.FONT_HIGHLIGHT);
			} else {
				entity.setSpecialEffect(EngineFX.FONT_NORMAL);
			}
		}

	}

	/**
	 * Display a menu
	 * 
	 * @param p_menu
	 *            (can't be null)
	 */
	public void displayMenu(Menu p_menu) {
		int sizeY = (p_menu.items.size() + 2) * Constantes.TEXTER_MENU_SIZEY;
		int startY = (Zildo.viewPortY - sizeY) / 2;
		if (!p_menu.displayed) {
			// Display menu's text
			setToDisplay_dialogMode(DialogMode.MENU);
			int posY = startY;
			removePreviousTextInFrame();
			// Title
			prepareTextInFrame(p_menu.title, Constantes.TEXTER_COORDINATE_X,
					posY);
			posY += 2 * Constantes.TEXTER_MENU_SIZEY;
			
			// Items
			itemsOnScreen.clear();
			for (ItemMenu item : p_menu.items) {
				Zone z = prepareTextInFrame(item.getText(),
						Constantes.TEXTER_COORDINATE_X, posY);
				posY += Constantes.TEXTER_MENU_SIZEY;
				// Store item location
				itemsOnScreen.put(item, z);
			}
			p_menu.displayed = true;
		}
		menuSequence.clear();
		int y = startY + (p_menu.selected + 2) * Constantes.TEXTER_MENU_SIZEY;
		alpha += 0.1f;
		int wave = (int) (10.0f * Math.sin(alpha));
		menuSequence.addSprite(FontDescription.FRAME_UPRIGHT, 40 + wave, y + 2);
		menuSequence.addSprite(FontDescription.FRAME_UPLEFT, Zildo.viewPortX
				- 40 - wave, y + 2);
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
		PersoZildo zildo = (PersoZildo) spriteDisplay.getZildo();
		if (zildo == null) {
			return;
		}

		int i;
		// Life
		guiSpritesSequence.addSprite(FontDescription.GUI_LIFE, 207, 10);
		for (i = 0; i < zildo.getMaxpv() / 2; i++) {
			int pv = zildo.getPv();
			FontDescription desc;
			if (i == pv >> 1 && pv % 2 == 1) {
				desc = FontDescription.GUI_HEARTHALF; // Half heart
			} else if (pv >> 1 <= i) {
				desc = FontDescription.GUI_HEARTEMPTY; // Empty heart
			} else {
				desc = FontDescription.GUI_HEART; // Full heart
			}
			guiSpritesSequence.addSprite(desc, 190 + ((i - 1) % 10) * 8,
					20 + 8 * ((i - 1) / 10));
		}

		// Money
		guiSpritesSequence.addSprite(FontDescription.GUI_RUPEE, 72, 10);
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
		displayNumber(countMoney, 3, 66, 20);

		// Bombs
		guiSpritesSequence.addSprite(FontDescription.GUI_BOMB, 110, 10);
		displayNumber(zildo.getCountBomb(), 2, 107, 20);

		// Arrows
		guiSpritesSequence.addSprite(FontDescription.GUI_ARROW, 149, 10);
		displayNumber(zildo.getCountArrow(), 2, 148, 20);

		// Keys
		guiSpritesSequence.addSprite(FontDescription.GUI_KEY, 41, 10);
		displayNumber(zildo.getCountKey(), 1, 40, 20);
	}

	private void displayNumber(int p_number, int p_numDigit, int p_x, int p_y) {
		int lastPos = p_x + p_numDigit * 7 - 7;
		for (int i = 0; i < p_numDigit; i++) {
			int j = p_number;
			if (i == 2) {
				j = j / 100;
			} else if (i == 1) {
				j = j / 10;
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

	// /////////////////////////////////////////////////////////////////////////////////////
	// clean
	// /////////////////////////////////////////////////////////////////////////////////////
	public void clean() {
		guiSpritesSequence.clear();
	}

	public boolean isVisibleMessageDisplay() {
		return visibleMessageDisplay;
	}

	public void setVisibleMessageDisplay(boolean visibleMessageDisplay) {
		this.visibleMessageDisplay = visibleMessageDisplay;
	}

	public boolean isEntireMessageDisplay() {
		return entireMessageDisplay;
	}

	public void setEntireMessageDisplay(boolean entireMessageDisplay) {
		this.entireMessageDisplay = entireMessageDisplay;
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
}