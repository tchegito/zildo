package zildo.client.gui;

import java.util.Iterator;

import zildo.client.ClientEngineZildo;
import zildo.client.SpriteDisplay;
import zildo.fwk.FilterCommand;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.PixelShaders;
import zildo.monde.SpriteModel;
import zildo.monde.decors.SpriteEntity;
import zildo.monde.persos.PersoZildo;
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
	
	private FilterCommand filterCommand;
	
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
		
		countMoney=0;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// draw
	///////////////////////////////////////////////////////////////////////////////////////
	// Main method for this class.
	// Should handle all events happening here.
	///////////////////////////////////////////////////////////////////////////////////////
	public void draw()
	{
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
		prepareTextInFrame(texte);
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
	void prepareTextInFrame(String texte)
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
		int nMaxLigne;
		boolean visibleFont;
		boolean center;
		int i;
		switch (toDisplay_dialogMode) {
		case DIALOGMODE_CLASSIC:
		default:
			nBank=SpriteBank.BANK_FONTES;
			sizeLine=Constantes.TEXTER_SIZELINE;
			nMaxLigne=3;
			visibleFont=false;
			center=false;
			break;
		case DIALOGMODE_TOPIC:
			nBank=SpriteBank.BANK_FONTES2;
			sizeLine=Constantes.TEXTER_TOPIC_SIZELINE;
			nMaxLigne=10;
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
				spr=EngineZildo.spriteManagement.getSpriteBank(nBank).get_sprite(nSpr[nLettre]);
				sizeCurrentWord+=(spr.getTaille_x() + 1);
			}
			nLettre++;
		}
		sizesLine[nLigne]=sizeCurrentLine + sizeCurrentWord;
	
		// 2) Display prepared sprites
		int x=Constantes.TEXTER_COORDINATE_X;;
		int y=Constantes.TEXTER_COORDINATE_Y;
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
							ClientEngineZildo.soundPlay.playSoundFX("AfficheTexteFin");
						}
					} else {
						visibleMessageDisplay=true;
						// If the text has another line to scroll, don't play sound
						if (!scrolling) {
							ClientEngineZildo.soundPlay.playSoundFX("AfficheTexteFin");
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
		// Re-initialize the gui's sprites sequence.
		// Each frame, we re-add the sprites to avoid doing test about what exactly changes
		// from last frame.
		clean();
	
		int i,j;
		// Life
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,91,207,10);
		for (i=1;i<zildo.getMaxpv() ;i++) {
			if (i==zildo.getPv() >> 1 && (zildo.getPv() % 2 == 0)) {
				j=14;
			} else if (zildo.getPv() >> 1 < i) {
				j=1;
			} else {
				j=0;
			}
			guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,78+j,190+((i-1) % 10) * 8,
													20+8*((i-1) / 10));
		}
	
		// Money
		guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,80,100,10);
		if (countMoney < zildo.getMoney()) {
			countMoney++;
		}
		for (i=0;i<3;i++) {
			j=countMoney;
			if (i==0) {
				j=j / 100;
			} else if (i==1) {
				j=j / 10;
			}
			j=j % 10;
			guiSpritesSequence.addSprite(SpriteBank.BANK_FONTES,81+j,94+i*8,20);
		}
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