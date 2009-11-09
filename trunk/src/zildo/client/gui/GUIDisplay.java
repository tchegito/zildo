package zildo.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.lwjgl.util.vector.Vector3f;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.client.SoundPlay.BankSound;
import zildo.client.gui.menu.ItemMenu;
import zildo.client.gui.menu.Menu;
import zildo.fwk.FilterCommand;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;

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

	public static final int DIALOGMODE_CLASSIC=1;
	public static final int DIALOGMODE_TOPIC=2;
	public static final int DIALOGMODE_MENU=3;
	
	// External variables for interacting with GUI
	private boolean toDisplay_dialoguing;
	private boolean toRemove_dialoguing;
	private boolean toDisplay_generalGui;
	private int toDisplay_dialogMode;		// Contient DIALOGMODE_CLASSIC ou DIALOGMODE_TOPIC

	// External flags for text display
	private boolean visibleMessageDisplay;	// FALSE=Visible text isn't display yet at screen
	private boolean entireMessageDisplay;	// FALSE=Entire sentence aren't display yet at screen.

	private int countMoney;
	
	private GUISpriteSequence textFontSequence;
	private GUISpriteSequence frameDialogSequence;
	private GUISpriteSequence guiSpritesSequence;
	private GUISpriteSequence menuSequence;
	
	private FilterCommand filterCommand;
	
	public float alpha;
	
	Stack<GameMessage> messageQueue;
	
	//////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	//////////////////////////////////////////////////////////////////////
	
	public GUIDisplay()
	{
		toDisplay_dialoguing=false;
		toDisplay_generalGui=false;
		toRemove_dialoguing=false;
	
		// Initialize screen filter
		filterCommand=ClientEngineZildo.filterCommand;
	
		textFontSequence=new GUISpriteSequence();
		frameDialogSequence=new GUISpriteSequence();
		guiSpritesSequence=new GUISpriteSequence();
		menuSequence = new GUISpriteSequence();
		
		countMoney=0;
		
		messageQueue=new Stack<GameMessage>();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// draw
	///////////////////////////////////////////////////////////////////////////////////////
	// Main method for this class.
	// Should handle all events happening here.
	///////////////////////////////////////////////////////////////////////////////////////
	public void draw()
	{
		// Re-initialize the gui's sprites sequence.
		// Each frame, we re-add the sprites to avoid doing test about what exactly changes
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
			toRemove_dialoguing=false;
			toDisplay_dialoguing=false;
		} else if (toDisplay_dialoguing) {
			// Draw frame and text inside it
			drawFrame();
		};
		
        drawConsoleMessages();
	}
	
	public void drawConsoleMessages() {
		int y=230;
		List<GameMessage> toRemove=new ArrayList<GameMessage>();
		for (GameMessage mess : messageQueue) {
			ClientEngineZildo.ortho.drawText(0,y,mess.text, new Vector3f(1.0f, 1.0f, 1.0f));
			if (mess.duration-- == 0) {
				toRemove.add(mess);
			}
			y-=8;
		}
		for (GameMessage mess :toRemove) {
			messageQueue.remove(mess);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// setText
	///////////////////////////////////////////////////////////////////////////////////////
	// Determine text to display in the frame on the bottom of the screen
	// and spawn fonts corresponding to the given text.
	///////////////////////////////////////////////////////////////////////////////////////
	public void setText(String texte, int dialogMode)
	{
		toDisplay_dialogMode=dialogMode;
		removePreviousTextInFrame();
		prepareTextInFrame(texte, Constantes.TEXTER_COORDINATE_X, Constantes.TEXTER_COORDINATE_Y);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getIndexCharacter
	///////////////////////////////////////////////////////////////////////////////////////
	// IN :character in text sequence
	// OUT:given character's font position in the FONTES.PNJ sprite bank
	///////////////////////////////////////////////////////////////////////////////////////
	static String caracteres_speciaux="-.,<>!?()'";
	int getIndexCharacter(char a) {
		if (a>='A' && a<='Z')
			return a-'A';
		if (a>='a' && a<='z')
			return a-'a'+ 26 + 12;
		if (a>='0' && a<='9')
			return a-'0' + 26*2 +12;
		for (int i=0;i<caracteres_speciaux.length();i++) {
			if (a==caracteres_speciaux.charAt(i))
				return i+26;
		}
	
		// Nothing found
		return 0;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// displayText
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:x,y and sequence to display
	///////////////////////////////////////////////////////////////////////////////////////
	/*displayText(int x, int y, String& texte)
	{
		int offsetX=0;
		SpriteEntity* lettre;
		Sprite* model;
		for (int i=0;i<texte.length();i++) {
			char a=texte[i];
			if (a==' ') {
				offsetX+=4;
			} else {
				int nSpr=getIndexCharacter(a);
				lettre=EngineZildo::spriteManagement.spawnFont(BANK_FONTES,nSpr,x+offsetX,y);
				model=lettre.sprModel;
				offsetX+= (model.taille_x+1);
			}
		}
	}
	*/
	///////////////////////////////////////////////////////////////////////////////////////
	// prepareTextInFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Display text inside a frame drawn by another method.
	// -.Build a sequence of characters corresponding to text format inside the text frame.
	//    Use '-1' as 'SPACE'
	//        '-2' as 'ENDOFLINE'
	// -.Spawn fonts at text place
	///////////////////////////////////////////////////////////////////////////////////////
	public void prepareTextInFrame(String texte, int p_posX, int p_posY)
	{
		// 1) Split sequence into list of words
		//    and measure size of text to render
		//int* nSpr=(int*)malloc(sizeof(int) * (texte.length() + 3));	// 3 for each carriage return
		int length=texte.length() + 10;
		int[] nSpr=new int[length];
		SpriteModel spr;
		int nLettre=0;
		int nLigne=0;
		int sizeCurrentWord=0;
		int sizeCurrentLine=0;
		int lastSpacePosition=-1;
		int[] sizesLine=new int[Constantes.MAX_TOPICS];
	
		// Interpret dialog Mode
		int nBank;
		int sizeLine;
		//int nMaxLigne;
		boolean visibleFont;
		boolean center;
		int i;
		switch (toDisplay_dialogMode) {
		case DIALOGMODE_CLASSIC:
		default:
			nBank=SpriteBank.BANK_FONTES;
			sizeLine=Constantes.TEXTER_SIZELINE;
			//nMaxLigne=3;
			visibleFont=false;
			center=false;
			break;
		case DIALOGMODE_MENU:
			nBank=SpriteBank.BANK_FONTES;
			sizeLine=Constantes.TEXTER_SIZELINE;
			visibleFont=true;
			center=true;
			break;
		case DIALOGMODE_TOPIC:
			nBank=SpriteBank.BANK_FONTES2;
			sizeLine=Constantes.TEXTER_TOPIC_SIZELINE;
			//nMaxLigne=10;
			visibleFont=true;
			center=true;
			break;
		}
	
		for (i=0;i<=texte.length();i++) {
			char a;
			if (i==texte.length()) {
				a=0;
			} else {
				a=texte.charAt(i);
			}
			if (a==' ' || a==0 || a=='#' || a=='\n') {
				if (sizeCurrentLine+sizeCurrentWord > Constantes.TEXTER_SIZEX || a=='\n') {
					// We must cut the line before the current word
					if (a=='\n') {
						sizesLine[nLigne]=sizeCurrentLine + sizeCurrentWord;
						sizeCurrentWord=0;
						nSpr[nLettre]=-2;
					} else {
						if (lastSpacePosition != -1) {	// Put 'ENDOFLINE' at the last space
							nSpr[lastSpacePosition]=-2;
						} else {	// No space from the beginning of the message
							nSpr[nLettre]=-2;
						}
					}
					nLigne++;
					sizeCurrentLine=0;
				}
				if  (a==' ') {
					sizeCurrentLine+=Constantes.TEXTER_SIZESPACE;		// Space size
					nSpr[nLettre]=-1;
					lastSpacePosition=nLettre;
				} else if (a!='\n') {	// 'ENDOFTEXT'
					break;
				}
				sizeCurrentLine+=sizeCurrentWord;
				sizeCurrentWord=0;
			} else {	// Regular character
				// Store sprite's index to display for this letter
				nSpr[nLettre]=getIndexCharacter(a);
				// Get sprite model to obtain horizontal size
				spr=ClientEngineZildo.spriteDisplay.getSpriteBank(nBank).get_sprite(nSpr[nLettre]);
				sizeCurrentWord+=(spr.getTaille_x() + 1);
			}
			nLettre++;
		}
		sizesLine[nLigne]=sizeCurrentLine + sizeCurrentWord;
	
		// 2) Display prepared sprites
		int x=p_posX;
		int y=p_posY;
		int offsetX=0;
		int offsetY=0;
		if (center) {
			offsetX+=(Constantes.TEXTER_SIZEX - sizesLine[0]) / 2;
		}
		nLigne=0;
		SpriteEntity lettre;
		for (i=0;i<nLettre;i++) {
			int indexSpr=nSpr[i];
			if (indexSpr==-1) {
				// Space
				offsetX+=Constantes.TEXTER_SIZESPACE;
			} else if (indexSpr==-2) {
				offsetX=0;
				offsetY+=sizeLine;
				nLigne++;
				if (center) {
					offsetX+=(Constantes.TEXTER_SIZEX - sizesLine[nLigne]) / 2;
				}
			} else {
				// Store font's pointer to easily remove it later and scroll into the frame
				lettre=textFontSequence.addSprite(nBank,indexSpr,x+offsetX,y+offsetY, visibleFont);
				spr=lettre.getSprModel();
				offsetX+=(spr.getTaille_x() + 1);
			}
		}
	
		visibleMessageDisplay=false;	// Say that the message is not complete yet at screen
		entireMessageDisplay=false;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// removePreviousTextInFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Remove current text from dialog's frame.
	///////////////////////////////////////////////////////////////////////////////////////
	void removePreviousTextInFrame()
	{
		textFontSequence.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// removeFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Remove the frame's corner
	///////////////////////////////////////////////////////////////////////////////////////
	void removeFrame()
	{
		frameDialogSequence.clear();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// drawFrame
	///////////////////////////////////////////////////////////////////////////////////////
	// Draw frame around displayed text
	///////////////////////////////////////////////////////////////////////////////////////
	private final int couleur_cadre[]={3,9,9,169,3};
	void drawFrame()
	{
		// Draw corner frame
		if ( !frameDialogSequence.isDrawn()) {
			frameDialogSequence.addSprite(SpriteBank.BANK_FONTES,26+12+26+10,  40,170);
			frameDialogSequence.addSprite(SpriteBank.BANK_FONTES,26+12+26+10+1,  280,170);
			frameDialogSequence.addSprite(SpriteBank.BANK_FONTES,26+12+26+10+2,  40,230);
			frameDialogSequence.addSprite(SpriteBank.BANK_FONTES,26+12+26+10+3,  280,230);
		}
	
		// Draw frame's bars
		for (int i=0;i<5;i++) {
			ClientEngineZildo.ortho.initDrawBox(false);
			ClientEngineZildo.ortho.boxOpti(47,170+i,233,1,couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(47,236-i,233,1,couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(40+i,177,1,53,couleur_cadre[i], null);
			ClientEngineZildo.ortho.boxOpti(286-i,177,1,53,couleur_cadre[i], null);
			ClientEngineZildo.ortho.endDraw();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// displayTextParts
	///////////////////////////////////////////////////////////////////////////////////////
	public void displayTextParts(int position, String sentence, boolean scrolling) {
		Iterator<SpriteEntity> it=textFontSequence.iterator();
		int j=0;
		char a=0;
	
		// We have to know how much font have to be enabled/disabled (with visibility)
		for (int i=0;i<position+1 ;i++) {
			if (i<sentence.length())
				a=sentence.charAt(i);
			if (a==' ' || i==sentence.length()) {
				for (int k=0;k< (i-j) && it.hasNext();k++) {
					SpriteEntity entity=it.next();
					if (entity.getScrY() < Constantes.TEXTER_COORDINATE_Y) {
						entity.setVisible(false);
					} else if (entity.getScrY() < (Constantes.TEXTER_COORDINATE_Y + (Constantes.TEXTER_NUMLINE*Constantes.TEXTER_SIZELINE) - 10 ) ) {
						entity.setVisible(true);
						if (i==sentence.length()) {
							entireMessageDisplay=true;
							ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexteFin);
						}
					} else {
						visibleMessageDisplay=true;
						// If the text has another line to scroll, don't play sound
						if (!scrolling) {
							ClientEngineZildo.soundPlay.playSoundFX(BankSound.AfficheTexteFin);
						}
					}
				}
				j=i+1;
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// scrollAndDisplayTextParts
	///////////////////////////////////////////////////////////////////////////////////////
	// Scroll every fonts one row to the height
	// And display fonts which are inside the frame
	///////////////////////////////////////////////////////////////////////////////////////
	public void scrollAndDisplayTextParts(int position, String sentence) {
		for (SpriteEntity entity : textFontSequence) {
			entity.setScrY(entity.getScrY() - Constantes.TEXTER_SIZELINE);
		}
	
		displayTextParts(position,sentence,false);
		visibleMessageDisplay=false;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	// displayTopics
	///////////////////////////////////////////////////////////////////////////////////////
	public void displayTopics(int selected) {
		for (SpriteEntity entity : textFontSequence) {
			int numLigne=(entity.getScrY() - Constantes.TEXTER_COORDINATE_Y) / Constantes.TEXTER_TOPIC_SIZELINE;
			if (numLigne == selected) {
				entity.setSpecialEffect(PixelShaders.ENGINEFX_FONT_HIGHLIGHT);
			} else {
				entity.setSpecialEffect(PixelShaders.ENGINEFX_FONT_NORMAL);
			}
		}
	
	}
	
	/**
	 * Display a menu
	 * @param p_menu (can't be null)
	 */
	public void displayMenu(Menu p_menu) {
		int sizeY=(p_menu.items.size() + 2) * Constantes.TEXTER_MENU_SIZEY;
		int startY=(Zildo.viewPortY - sizeY) / 2;
		if (!p_menu.displayed) {
			// Display menu's text
			ClientEngineZildo.guiDisplay.setToDisplay_dialogMode(GUIDisplay.DIALOGMODE_MENU);
            int posY = startY;
            removePreviousTextInFrame();
            // Title
            prepareTextInFrame(p_menu.title, Constantes.TEXTER_COORDINATE_X, posY);
			posY+=2*Constantes.TEXTER_MENU_SIZEY;
			// Items
			for (ItemMenu item : p_menu.items) {
				prepareTextInFrame(item.getText(), Constantes.TEXTER_COORDINATE_X, posY);
				posY+= Constantes.TEXTER_MENU_SIZEY;
			}
			p_menu.displayed=true;
		}
		menuSequence.clear();
		int nSpr=26+12+26+10;
		int y=startY + (p_menu.selected+2) * Constantes.TEXTER_MENU_SIZEY;
		alpha+=0.1f;
		int wave=(int) (10.0f*Math.sin(alpha));
		menuSequence.addSprite(SpriteBank.BANK_FONTES, nSpr, 40+wave,y+2);
		menuSequence.addSprite(SpriteBank.BANK_FONTES, nSpr+1, Zildo.viewPortX - 40-wave,y+2);
	}

	public void endMenu() {
		menuSequence.clear();
		removePreviousTextInFrame();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeIn
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeIn() {
		filterCommand.fadeIn();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeOut
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeOut() {
		filterCommand.fadeOut();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isFadeOver
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isFadeOver() {
		return filterCommand.isFadeOver();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// drawGeneralGUI
	///////////////////////////////////////////////////////////////////////////////////////
	void drawGeneralGUI() {
		SpriteDisplay spriteDisplay = ClientEngineZildo.spriteDisplay;
		PersoZildo zildo = (PersoZildo) spriteDisplay.getZildo();
		if (zildo == null) {
			return;
		}
		
		int i,j;
		// Life
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,91,207,10);
        for (i = 0; i < zildo.getMaxpv(); i++) {
            int pv = zildo.getPv();
            if (i == pv >> 1 && pv % 2 == 1) {
                j = 14; // Half heart
            } else if (pv >> 1 <= i) {
                j = 1; // Empty heart
            } else {
                j = 0; // Full heart
            }
			guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,78+j,190+((i-1) % 10) * 8,
													20+8*((i-1) / 10));
		}
	
		// Money
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,80,72,10);
		if (countMoney < zildo.getMoney()) {
			countMoney++;
			if (zildo.getMoney() - countMoney % 20 == 0) {
				EngineZildo.soundManagement.broadcastSound(BankSound.ZildoGagneArgent, zildo);
			}
		}
		displayNumber(countMoney, 3, 66, 20);
		
		// Bombs
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,93,110,10);
		displayNumber(zildo.countBomb, 2, 107, 20);

		// Arrows
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,94,149,10);
		displayNumber(zildo.countArrow, 2, 148, 20);

	}
	
	private void displayNumber(int p_number, int p_numDigit, int p_x, int p_y) {
		int lastPos=p_x + p_numDigit * 7 - 7;
		for (int i=0;i<p_numDigit;i++) {
			int j=p_number;
			if (i==2) {
				j=j / 100;
			} else if (i==1) {
				j=j / 10;
			}
			j=j % 10;
			guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,81+j,lastPos-i*7,p_y);
		}
	}
	
	public void displayMessage(String text) {
		messageQueue.add(new GameMessage(text));
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// clean
	///////////////////////////////////////////////////////////////////////////////////////
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

	public int getToDisplay_dialogMode() {
		return toDisplay_dialogMode;
	}

	public void setToDisplay_dialogMode(int toDisplay_dialogMode) {
		this.toDisplay_dialogMode = toDisplay_dialogMode;
	}

}