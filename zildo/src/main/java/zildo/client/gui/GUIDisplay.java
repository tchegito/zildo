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

package zildo.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.PlatformDependentPlugin;
import zildo.client.SpriteDisplay;
import zildo.client.gui.menu.CompassMenu;
import zildo.client.gui.menu.HallOfFameMenu;
import zildo.client.gui.menu.StartMenu;
import zildo.client.sound.BankSound;
import zildo.client.stage.CreditStage;
import zildo.client.stage.MenuStage;
import zildo.client.stage.TitleStage;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.GFXBasics;
import zildo.fwk.gfx.Ortho;
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
import zildo.monde.sprites.desc.ElementDescription;
import zildo.monde.sprites.desc.FontDescription;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.monde.util.Vector3f;
import zildo.monde.util.Vector4f;
import zildo.monde.util.Zone;
import zildo.server.MultiplayerManagement;
import zildo.server.state.PlayerState;

// Here we draw the Graphic User Interface. It means we paint screen in last moment,
// after all engines : Tile and Sprite.
// We draw:
// -frame with text inside
// -life, money
// -inventory
// -extra informations, various animations ...

// There is 2 methods called on each frame:
// -prepareDraw
// -draw

// Other class communicate with this one by events:
// -toDisplay_dialoguing
// -toRemove_dialoguing

public class GUIDisplay {

	/** There's different rendering on menus: for example, opening menu is subject to fade.
	 * Other ones don't, because ingame menus should always been highlighted, even if there's a nightfilter, or player is inventoring,
	 * causing a semifade.
	 */
	public enum DialogMode {
		CLASSIC, OPENING_MENU, MENU, CREDITS, HALLOFFAME, INFO /*TitleStage*/, BUY, ADVENTURE_MENU /*CompassMenu*/ , TEXTER;
		
		public boolean isScript() {
			return true; //this == CLASSIC || this == CREDITS;
		}
		
		public boolean isBig() {
			switch (this) {
			case MENU:
			case INFO:
			case BUY:
			case OPENING_MENU:
				return true;
			default:
				return false;
			}
		}
		
		public boolean isMenu() {
			switch (this) {
			case MENU:
			case HALLOFFAME:
			case ADVENTURE_MENU:
			case OPENING_MENU:
				return true;
			default:
				return false;
			}
		}
	}
	
	// Alpha channel for virtual pad (specific Android) : range is 0..255
	final static int alphaPad = 128;
	
	final static char TXT_END_OF_LINE = (char)-2;
	public final static char TXT_CHANGE_COLOR = (char)-3;
	
	// External variables for interacting with GUI
	private boolean toDisplay_dialoguing;
	private boolean toRemove_dialoguing;
	private boolean toDisplay_generalGui;
	private boolean toDisplay_scores;
	private boolean toDisplay_adventureMenu;

	private DialogMode toDisplay_dialogMode;

	// External flags for text display

	DialogContext dialogContext;
	DialogDisplay dialogDisplay;
	
	private int countMoney;

	private GUISpriteSequence textDialogSequence; // All fonts displayed in dialog
	private GUISpriteSequence textMenuSequence; // All fonts displayed in menu
	private GUISpriteSequence frameDialogSequence; // Yellow frame for display dialog
	private GUISpriteSequence guiSpritesSequence; // All sprites designing the GUI
	private GUISpriteSequence creditSequence; // Credits
	private GUISpriteSequence infoSequence; // For displaying infos	(see TitleStage)
	private GUISpriteSequence adventureSequence; // For displaying adventure menu

	private ScreenConstant sc;
	
	// Menu items location (for Android)
	private Map<ItemMenu, Zone> itemsOnScreen = new HashMap<ItemMenu, Zone>();
	
	private int arrowSprite;
	private float alpha;
	
	List<GameMessage> messageQueue;

	final int padSizeX;
	final int buttonSizeX;
	
	int texterHeight;	// Max Y coordinates in texter frame
	int texterSizeY;
	
	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	int sizeX;
	int sizeY;
	int posX1;
	int posY1;
	
	public GUIDisplay() {
		toDisplay_dialoguing = false;
		toDisplay_generalGui = false;
		toRemove_dialoguing = false;

		// Initialize every sequence
		textDialogSequence = new GUISpriteSequence();
		textMenuSequence = new GUISpriteSequence();
		frameDialogSequence = new GUISpriteSequence();
		guiSpritesSequence = new GUISpriteSequence();
		creditSequence = new GUISpriteSequence();
		infoSequence = new GUISpriteSequence();
		adventureSequence = new GUISpriteSequence();
		
		countMoney = 0;

		messageQueue = new CopyOnWriteArrayList<GameMessage>();
		
		initTransco();
		
		// Screen constants
		sc = ClientEngineZildo.screenConstant;
		sizeX = sc.TEXTER_SIZEX;
		sizeY = sc.TEXTER_SIZELINE_SCRIPT * sc.TEXTER_NUMLINE + 2;
		posX1 = sc.TEXTER_COORDINATE_X - 10;
		posY1 = sc.TEXTER_COORDINATE_Y - 10;
		
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
			+ "-.,<>!?()'#$çéêèàûîâôüöäïëù" + "abcdefghijklmnopqrstuvwxyz"
			+ "0123456789~£§/:%;";
	final Map<Character, Integer> mapTranscoChar = new HashMap<Character, Integer>();

	public static final int[] scriptLegibility = new int[transcoChar.length() + 6];
	public static final int[] scriptBigLegibility = new int[220]; //transcoChar.length() + 50];
	
	private void initTransco() {
		for (int i = 0; i < transcoChar.length(); i++) {
			mapTranscoChar.put(transcoChar.charAt(i), i);
		}
		// Extra characters
		mapTranscoChar.put('^', FontDescription.GUI_BLUEDROP.getNSpr() - transcoChar.length());
		mapTranscoChar.put('¤', FontDescription.GUI_RUPEE.getNSpr() - transcoChar.length());
		mapTranscoChar.put('µ', FontDescription.SQUIRREL.getNSpr());
		
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
		scriptLegibility[transcoChar.indexOf("u")] = 1;
		scriptLegibility[transcoChar.indexOf("g")] = 1;

		for (int i=0;i<scriptLegibility.length;i++) {
			scriptBigLegibility[i] = scriptLegibility[i] * 2;
		}
		scriptBigLegibility[transcoChar.indexOf("Q")]--;
		scriptBigLegibility[transcoChar.indexOf("F")] = 3;
		scriptBigLegibility[transcoChar.indexOf("C")] = 1;
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
		scriptBigLegibility[transcoChar.indexOf("u")] = 2;
		scriptBigLegibility[transcoChar.indexOf("r")] = 1;
		scriptBigLegibility[transcoChar.indexOf("b")] = 1;
		scriptBigLegibility[transcoChar.indexOf("e")] = 1;
		scriptBigLegibility[transcoChar.indexOf("m")] = 1;
		scriptBigLegibility[transcoChar.indexOf("n")] = 1;
		scriptBigLegibility[transcoChar.indexOf("f")] = 1;
		scriptBigLegibility[transcoChar.indexOf("h")] = 1;
		scriptBigLegibility[transcoChar.indexOf("x")] = 1;
		scriptBigLegibility[transcoChar.indexOf("a")] = 2;
		scriptBigLegibility[transcoChar.indexOf("v")] = 1;

		arrowSprite = transcoChar.length() + mapTranscoChar.get('~');
	}

	int getIndexCharacter(char a) {
		Integer c = mapTranscoChar.get(a);
		if (c == null) {
			return 0;	// 'A' instead of NullPointer !
		}
		return c.intValue();
	}
	
	/** Called before updating sprite entities on display **/
	public void prepareDraw(boolean isMenu) {
		if (!isMenu && toDisplay_dialogMode != DialogMode.TEXTER) {
			// Re-initialize the gui's sprites sequence.
			// Each frame, we re-add the sprites to avoid doing test about what
			// exactly changes from last frame.
			clearSequences(GUISequence.GUI);
	
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
			prepareDrawFrame();
		}
		if (toDisplay_adventureMenu) {
			// In this mode, we have to remove any dialogs => best is to forbid compass during conversations
			displayAdventureMenu();
		} else if (adventureSequence.isDrawn()) {
			adventureSequence.clear();
			frameDialogSequence.clear();
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////////////////
	// draw
	// /////////////////////////////////////////////////////////////////////////////////////
	// Main method for this class.
	// Should handle all events happening here.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void draw() {
		if (toDisplay_dialoguing) {
			// Draw frame and text inside it
			drawBox(posX1, posY1, sizeX, sizeY, true);
		}

		if (toDisplay_scores) {
			drawScores();
		}

		if (toDisplay_dialogMode == DialogMode.TEXTER) {
			// Frame under texter
			if (texterSizeY < 200) {
				// Note: this is not vertically centered because drawBox adds 10 to height.
				drawBox(sc.BIGTEXTER_X-10, (Zildo.viewPortY - texterSizeY)/2, 
						sc.BIGTEXTER_WIDTH, texterSizeY, false);
			} else {
				drawBox(sc.BIGTEXTER_X-10, sc.BIGTEXTER_Y-10, 
						sc.BIGTEXTER_WIDTH, Zildo.viewPortY - 38, false);
			}
		}
		
		drawConsoleMessages();
	}

	public void drawConsoleMessages() {
		int y = 230;
		List<GameMessage> toRemove = new ArrayList<GameMessage>();
		for (GameMessage mess : messageQueue) {
			ClientEngineZildo.ortho.drawText(23, y, mess.text, new Vector3f(1.0f, 1.0f, 1.0f));
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
		prepareTextInFrame(texte, sc.TEXTER_COORDINATE_X, sc.TEXTER_COORDINATE_Y, false);
	}

	/**
	 * Build a sequence of characters corresponding to text format inside the
	 * text frame.<ul>
	 * <li>Use '-1' as 'SPACE'</li>
	 * <li>'-2' as 'ENDOFLINE'</li>
	 * <li>'-3' as 'START/END of people name'</li></ul>
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
		// BEWARE! More than 256 lines will cause it to crash !!! 
		//TODO: See how we can do better (maybe this array isn't really useful ?)
		int[] sizesLine = new int[256]; //Constantes.TEXTER_NUMLINE * 3];	// Total rows (with scroll)

		// Interpret dialog Mode
		int nBank;
		int sizeLine;
		// int nMaxLigne;
		boolean visibleFont;
		boolean center = false;
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
		EngineFX regular = EngineFX.NO_EFFECT;
		
		switch (toDisplay_dialogMode) {
		case CLASSIC:
		default:
			visibleFont = false;
			center = false;
			if (texte.indexOf(TXT_CHANGE_COLOR) != -1) {
				fx = EngineFX.FONT_PEOPLENAME;
			}
			break;
		case ADVENTURE_MENU:
			seq = adventureSequence;
			visibleFont = true;
			center = true;
			break;
		case OPENING_MENU:
			fx = EngineFX.NO_EFFECT;
		case MENU:
		case HALLOFFAME:
			seq = textMenuSequence;
		case BUY:
			visibleFont = true;
			center = true;
			break;
		case CREDITS:
			center = true;
		case TEXTER:
			visibleFont = true;
			seq = creditSequence;
			regular = EngineFX.CLIP;
			fx = regular;	// To get the first line clipped
			width = sc.BIGTEXTER_WIDTH;
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
					if (a == '\n' && sizeCurrentLine + sizeCurrentWord <= width) {	// Current line has enough space to fit the last word
						sizesLine[nLigne] = sizeCurrentLine + sizeCurrentWord;
						sizeCurrentWord = 0;
						nSpr[nLettre] = TXT_END_OF_LINE;
					} else {	// Not enough space on this line, break line before last word
						sizesLine[nLigne] = sizeCurrentLine;
						if (lastSpacePosition != -1) { // Put 'ENDOFLINE' at the last space
							nSpr[lastSpacePosition] = TXT_END_OF_LINE;
							nSpr[nLettre] = TXT_END_OF_LINE;
						} else { // No space from the beginning of the message
							nSpr[nLettre] = TXT_END_OF_LINE;
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
			} else if (a == TXT_CHANGE_COLOR) {
				nSpr[nLettre] = TXT_CHANGE_COLOR;
			} else { // Regular character
				// Store sprite's index to display for this letter
				nSpr[nLettre] = getIndexCharacter(a);
				// Get sprite model to obtain horizontal size
				spr = ClientEngineZildo.spriteDisplay.getSpriteBank(nBank).get_sprite(nSpr[nLettre] + offsetNSpr);
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
			} else if (indexSpr == TXT_END_OF_LINE) {
				offsetX = 0;
				offsetY += sizeLine;
				nLigne++;
				if (center) {
					offsetX = (sc.TEXTER_SIZEX - sizesLine[nLigne]) / 2;
				}
			} else if (indexSpr == TXT_CHANGE_COLOR) {
				// Toggle color: people's name
				if (fx != EngineFX.FONT_PEOPLENAME) {
					fx = EngineFX.FONT_PEOPLENAME;
				} else {
					fx = regular;
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
	// prepareDrawFrame
	// /////////////////////////////////////////////////////////////////////////////////////
	// Prepare sprite sequences to draw frame around displayed text
	// /////////////////////////////////////////////////////////////////////////////////////
	private final int couleur_cadre[] = {48, 149, 150, 149, 48};
	//private final int couleur_cadre[] = {48, 239, 238, 239, 48}; //{ 3, 203, 204, 203, 3 };

	void prepareDrawFrame() {
		// Draw frame's bars
		prepareDrawBox(posX1, posY1, sizeX, sizeY, true);
		
		// Draw corner frame
		if (frameDialogSequence.isDrawn()) {
			// Animate arrow indicating next dialog, if necessary
			SpriteEntity entity = frameDialogSequence.get(4);
			dialogDisplay.animateArrow(entity);
		}
	}
	
	int x2;
	int y2;
	int fadeLevel;
	
	private void drawBox(int x, int y, int width, int height, boolean arrow) {

		Ortho ortho = ClientEngineZildo.ortho;
		ortho.enableBlend();
		ortho.initDrawBox(false);

		if (ClientEngineZildo.filterCommand.getActiveFade() != null) {
			fadeLevel = 255 - ClientEngineZildo.filterCommand.getFadeLevel();
		}
		for (int i = 0; i < 5; i++) {
			int col = couleur_cadre[i];
			Vector4f v = new Vector4f(GFXBasics.getColor(col));
			v.w = fadeLevel;
			v = v.scale(1.0f / 256.0f);
			ortho.boxOpti(x+7, y + i, width+4, 1, 0, v);
			ortho.boxOpti(x+6, y2+6 - i, width+4, 1, 0, v);
			ortho.boxOpti(x + i, y+7, 1, height+3, 0, v);
			ortho.boxOpti(x2+6 - i, y+7, 1, height+3, 0, v);
		}
		ortho.disableBlend();
		ortho.endDraw();
		ortho.enableBlend();
		ortho.box(x+5, y+5, width+7, height+7, 0, new Vector4f(0.4f, 0.15f, 0.12f, 0.7f * fadeLevel / 256f));
		ortho.disableBlend();
	}
	
	/** Draw a frame with transparent blue box inside, according to the current fade level **/
	private void prepareDrawBox(int x, int y, int width, int height, boolean arrow) {
		x2 = x + width + 10;
		y2 = y + height + 10;
		fadeLevel = 255;
		
		// Draw corner frame
		if (!frameDialogSequence.isDrawn()) {
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_LEFT, x - 3, y - 3);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_RIGHT, x2, y - 3);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_RIGHT, x - 3, y2 - 4, Reverse.ALL);
			frameDialogSequence.addSprite(FontDescription.FRAME_CORNER_LEFT, x2 + 0, y2 - 4, Reverse.ALL);
			
			if (arrow) {
				frameDialogSequence.addSprite(SpriteBank.BANK_FONTES, arrowSprite, 
						0,300,	// No matters coordinates, they will be update by dialogDisplay#animateArrow 
						false, 255);
			}
		}
		
		for (SpriteEntity se : frameDialogSequence) {
			se.setAlpha(fadeLevel);
		}
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
			if (entity != null && entity.getScrY() < sc.TEXTER_COORDINATE_Y) {
				entity.setVisible(false);
			} else if (entity != null && entity.getScrY() < (sc.TEXTER_BOTTOM_Y)) {
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

	/** Display every letters in the dialog frame, and return TRUE if message is complete. **/
	public boolean skipDialog() {
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
		return entire;
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
	 * Display scrolling credits (only used by {@link CreditStage})
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
	
	public void displayTexter(String text, int pos, int fadeLevel) {
		clearSequences(GUISequence.GUI);

		setToDisplay_dialogMode(DialogMode.TEXTER);
		if (creditSequence.isEmpty()) {
			prepareTextInFrame(text, sc.BIGTEXTER_X, sc.BIGTEXTER_Y, false);
			// Determine max height
			int maxY = 0;
			for (SpriteEntity entity : creditSequence) {
				maxY = Math.max(maxY, entity.getScrY());
			}
			texterHeight = Math.max(maxY - sc.BIGTEXTER_HEIGHT, 0);
			// Move all sprites inside the future frame
			texterSizeY = maxY + sc.TEXTER_SIZELINE - sc.BIGTEXTER_Y;
			if (texterSizeY < sc.BIGTEXTER_HEIGHT) {
				for (SpriteEntity entity : creditSequence) {
					entity.y += (Zildo.viewPortY - texterSizeY - sc.TEXTER_SIZELINE)/2;
				}			
			}
		}
		for (SpriteEntity entity : creditSequence) {
			// In texter mode, we display only fonts inside the texter frame
			entity.setScrY((int) entity.y - pos);
			int y = (int) entity.getScrY();
			boolean vis = y > 8 && y < (sc.BIGTEXTER_HEIGHT + sc.BIGTEXTER_Y);
			entity.setVisible(vis);
			// Do the fade process
			entity.setAlpha(fadeLevel);
		}
	}
	
	public int getTexterHeight() {
		return texterHeight;
	}
	
	/** Used only by {@link TitleStage} **/
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
	 * Display a menu (only used by {@link MenuStage}). Called each frame during the phase where a menu is proposed to the user.
	 * 
	 * @param p_menu
	 *            (can't be null)
	 */
	public void displayMenu(Menu p_menu, int fadeLevel) {
		int titleSize = p_menu.title == null ? 4 : 2;
		int sizeY = (p_menu.items.size() + titleSize) * sc.TEXTER_MENU_SIZEY;
		int startY = (Zildo.viewPortY - sizeY) / 2;

		if (!p_menu.displayed) {
			// Display menu's text
			if (p_menu instanceof HallOfFameMenu) {
				setToDisplay_dialogMode(DialogMode.HALLOFFAME);
			} else if (p_menu instanceof CompassMenu) {
				setToDisplay_dialogMode(DialogMode.ADVENTURE_MENU);
			} else if (p_menu instanceof StartMenu) {
				setToDisplay_dialogMode(DialogMode.OPENING_MENU);
			} else {
				setToDisplay_dialogMode(DialogMode.MENU);
			}
			int posY = startY;
			removePreviousTextInFrame();
			// Title
			if( p_menu.title != null) {
				prepareTextInFrame(p_menu.title, sc.TEXTER_COORDINATE_X,
						posY, false);
			}
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
		int y = startY + (p_menu.getSelected() + 2) * sc.TEXTER_MENU_SIZEY;
		alpha += 0.1f;
		
		GUISpriteSequence seq = textMenuSequence;
		if (p_menu instanceof CompassMenu) {
			seq = adventureSequence;
		}
		// Zoom the selected item
		for (SpriteEntity se : seq) {
			if (se.getScrY() == y) {
				//se.setScrY(y + (int) (5f*Math.sin(alpha)));
				int variation = (int) (20f*Math.sin(1.6f*alpha));
				se.zoom = 255 + variation;
				int distFromCenter = se.getAjustedX() - Zildo.viewPortX / 2;
				se.setScrX(Zildo.viewPortX / 2 + (int) (distFromCenter * (1f + variation / 500f)) );
				se.setSpecialEffect(EngineFX.YELLOW_HALO);
			} else {
				se.setSpecialEffect(EngineFX.FOCUSED);
				se.zoom = 255;
			}
			

		}
		// Do the fade process
		for (SpriteEntity se : seq) {
			if (se.getAlpha() == fadeLevel)
				break;
			se.setAlpha(fadeLevel);
		}
		if (p_menu instanceof CompassMenu) {
			setToDisplay_adventureMenu(true);
		}
	}

	public void endMenu() {
		removePreviousTextInFrame();
		// Put back in default mode
		toDisplay_dialogMode = DialogMode.CLASSIC;
		toDisplay_adventureMenu = false;
		removeFrame();
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
		final int DROPS_X = 300 - 28; //248;

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

			if (zildo.getCountNettleLeaf() >= 0) {
				guiSpritesSequence.addSprite(ElementDescription.NETTLE_LEAF, 241, GUI_Y + 1);
				displayNumber(zildo.getCountNettleLeaf(), 2, 231, GUI_Y);
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
			// Gear should stay always on Android screen
			guiSpritesSequence.addSprite(FontDescription.GEAR, Zildo.viewPortX - 30, 0);
			if (ClientEngineZildo.client.isDisplayedAndroidUI()) {
				int curAlpha = alphaPad;
				if (dialogDisplay.isDialoguing()) {
					curAlpha>>=2;
				}
				Point crossCenter = null;
				boolean movingCross = ClientEngineZildo.client.isMovingCross();
				if (movingCross) {
					crossCenter = ClientEngineZildo.client.getCrossCenter();
					Point drag = ClientEngineZildo.client.getDraggingTouch();
					if (drag != null) {
						guiSpritesSequence.addSprite(FontDescription.TOUCH_AURA, drag.x-16, drag.y-16);
					}
				} else {
					crossCenter = new Point(10 + (80/2), Zildo.viewPortY - (80/2));
				}
				if (crossCenter != null) {
					int x1 = crossCenter.x - (80/2);
					if (!movingCross) {
						x1 = computeForLeftHanded(x1, FontDescription.VIRTUAL_PAD);
					}
					guiSpritesSequence.addSprite(FontDescription.VIRTUAL_PAD, x1, crossCenter.y -(80/2), curAlpha);
				}
				int x2 = computeForLeftHanded(Zildo.viewPortX - 24 - 16 + 2, FontDescription.BUTTON_Y);
				int x3 = computeForLeftHanded(Zildo.viewPortX - 48 - 16 - 1, FontDescription.BUTTON_X);
				guiSpritesSequence.addSprite(FontDescription.BUTTON_Y, x2, Zildo.viewPortY-70 - 3, curAlpha);
				guiSpritesSequence.addSprite(FontDescription.BUTTON_X, x3, Zildo.viewPortY-40 + 8, curAlpha);
				}
		}
		// Display compass with low alpha if it isn't available
		guiSpritesSequence.addSprite(FontDescription.COMPASS, 48, 0, !isToDisplay_compassItem() ? 50 : 255);
	}

	private int computeForLeftHanded(int x, FontDescription desc) {
		if (ClientEngineZildo.client.isLeftHanded()) {
			switch (desc) {
			case VIRTUAL_PAD:
				return Zildo.viewPortX - x - padSizeX;
			case BUTTON_X:
			case BUTTON_Y:
				return Zildo.viewPortX - x - buttonSizeX;
			default:
				break;
			}
		}
		return x;
	}

	/** Display numeric representation as digit sprites **/
	private void displayNumber(int p_number, int p_numDigit, int p_x, int p_y) {
		if (p_number < 0) return;	// Silently exit, but this should never happen !
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
			FontDescription desc = FontDescription.values()[FontDescription.N_0.ordinal() + j];

			guiSpritesSequence.addSprite(desc, lastPos - i * 7, p_y);
		}
	}

	public void displayMessage(String text) {
		messageQueue.add(0, new GameMessage(text));
	}
	
	public void clearMessages() {
		messageQueue.clear();
	}

	private void displayAdventureMenu() {
		drawBox(50, (Zildo.viewPortY - 16 *5) / 2-2, 200, 16 * 4, false);
		if (toDisplay_dialogMode != DialogMode.ADVENTURE_MENU) {
			setToDisplay_dialogMode(DialogMode.ADVENTURE_MENU);
			adventureSequence.clear();
			ClientEngineZildo.client.handleMenu(new CompassMenu());
		}
	}

	public void setupHero(PersoPlayer zildo) {
		if (zildo != null) {
			countMoney = zildo.getMoney();
		}
	}

	/**
	 * Reinit GUI when a game is finished, to be ready next time.
	 */
	public void clearGui() {
		setToDisplay_generalGui(false);
		setToDisplay_dialoguing(false);
		countMoney = 0;
		clearSequences(GUISequence.all());
	}
	
	public boolean isToDisplay_dialoguing() {
		return toDisplay_dialoguing;
	}

	public void setToDisplay_dialoguing(boolean p_active) {
		toDisplay_dialoguing = p_active;
	}

	/** Forbid compass access in two cases:
	 * -dialog frame is displayed
	 * -hero is dialoguing with someone (transition to buy action removes frame for a short period)
	 */
	public boolean isToDisplay_compassItem() {
		if (isToDisplay_dialoguing())
			return false;
		PersoPlayer hero = (PersoPlayer) ClientEngineZildo.spriteDisplay.getZildo();
		return hero != null && hero.getDialoguingWith() == null && !hero.isInventoring();
	}
	
	public boolean isToRemove_dialoguing() {
		return toRemove_dialoguing;
	}

	public void setToRemove_dialoguing(boolean p_active) {
		toRemove_dialoguing = p_active;
	}

	public boolean isToDisplay_generalGui() {
		return toDisplay_generalGui;
	}

	public void setToDisplay_generalGui(boolean p_active) {
		toDisplay_generalGui = p_active;
	}

	public DialogMode getToDisplay_dialogMode() {
		return toDisplay_dialogMode;
	}

	public void setToDisplay_dialogMode(DialogMode p_active) {
		toDisplay_dialogMode = p_active;
	}

	public void setToDisplay_scores(boolean p_active) {
		toDisplay_scores = p_active;
	}
	public void setToDisplay_adventureMenu(boolean p_active) {
		toDisplay_adventureMenu = p_active;
	}
	
	/**
	 * Returns item on given location, if there's any. NULL otherwise.
	 * @param x
	 * @param y
	 * @return item (or NULL)
	 */
	public ItemMenu getItemOnLocation(int x, int y) {
		// Duplicate entries because 'displayMenu' method may modify it during our iteration
		// We add a synchronize block, because another set creation would also trigger concurrentmodificationexception
		synchronized (itemsOnScreen) {
			for (Entry<ItemMenu, Zone> e : itemsOnScreen.entrySet()) {
				if (e.getValue().isInto(x, y)) {
					return e.getKey();
				}
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
		ClientEngineZildo.ortho.drawText(-1, posY + 3, "scores", new Vector3f(1.0f, 1.0f, 1.0f));
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
		ClientEngineZildo.ortho.box(posX, posY, sizeX, sizeY, 0, new Vector4f(0.3f, 0.2f, 0.4f, 0.2f));
		ClientEngineZildo.ortho.disableBlend();
		ClientEngineZildo.ortho.boxv(posX, posY, sizeX, sizeY, 4, new Vector4f(0.9f, 0.7f, 0.4f, 0.4f));
		ClientEngineZildo.ortho.boxv(posX - 1, posY - 1, sizeX + 2, sizeY + 2, 4, new Vector4f(0.8f, 0.6f, 0.4f, 0.4f));

	}
	
	public void manageDialog() {
		if (dialogDisplay.isDialoguing()) {
			dialogDisplay.manageDialog();
		}		
	}
	
	public boolean launchDialog(List<WaitingDialog> p_queue) {
		return dialogDisplay.launchDialog(p_queue);
	}
	
	public void clearSequences(GUISequence... seqKinds) {
		for (GUISequence kind : seqKinds) {
			switch (kind) {
			case DIALOG:
				textDialogSequence.clear();
				dialogDisplay.actOnDialog(null, CommandDialog.STOP);
				break;
			case TEXT_MENU:
				textMenuSequence.clear();
				break;
			case FRAME_DIALOG:
				frameDialogSequence.clear();
				break;
			case GUI:
				guiSpritesSequence.clear();
				break;
			case CREDIT:
				creditSequence.clear();
				break;
			case INFO:
				infoSequence.clear();
				toDisplay_dialogMode = DialogMode.CLASSIC;
				break;
			}
		}
	}
}