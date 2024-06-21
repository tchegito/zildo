package zildo.client.stage;

import static zildo.client.ClientEngineZildo.guiDisplay;
import static zildo.client.gui.GUIDisplay.TXT_CHANGE_COLOR;

import java.util.List;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.client.gui.GUISequence;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.HistoryRecord;
import zildo.monde.quest.QuestLog;
import zildo.server.EngineZildo;

public class TexterStage extends GameStage {
	
	String wholeText;
	KeyboardHandler kbHandler;
	int position;
	int texterHeight;	// Denormalisation of GUIDisplay.getTexterHeight()
	Client client;
	
	public enum TexterKind {
		LAST_DIALOGS,
		GUIDE,
		QUEST_LOG
	}
	
	public TexterStage(TexterKind kind) {
		switch (kind) {
		case QUEST_LOG:
			wholeText = new QuestLog().getDisplayString();
			break;
		case LAST_DIALOGS:
			List<HistoryRecord> records = EngineZildo.game.getLastDialog();
			wholeText = HistoryRecord.getDisplayString(records);
			break;
		case GUIDE:
			// Surround hero name with symbols asking color change
			String heroName = UIText.getCharacterName();
			heroName = TXT_CHANGE_COLOR + heroName + TXT_CHANGE_COLOR;
			wholeText = UIText.getMenuText("guide.txt", heroName);
			break;
		}
		kbHandler = Zildo.pdPlugin.kbHandler;
		position = 0;
        client = ClientEngineZildo.getClientForGame();
	}
	
	@Override
	public void launchGame() {

	}
	
	@Override
	public void renderGame() {
		if (!done) {
			guiDisplay.displayTexter(wholeText, position, client.getMenuTransition().getFadeLevelMenu());
			texterHeight = guiDisplay.getTexterHeight();
		}
	}
	
	@Override
	public void endGame() {
		guiDisplay.clearSequences(GUISequence.CREDIT, GUISequence.FRAME_DIALOG);
		guiDisplay.setToDisplay_dialogMode(DialogMode.CLASSIC);
		done = true;
	}
	
	@Override
	public void updateGame() {
		// If user presses one of the 'quit' key, then leave
    	boolean askQuit = kbHandler.isKeyPressed(Keys.ESCAPE);
    	askQuit |= kbHandler.isKeyPressed(Keys.COMPASS);
    	askQuit |= kbHandler.isKeyPressed(Keys.TOUCH_BACK);
    	
		if (client.getMenuTransition().isCurrentOver()) {
			endGame();
		}
		
    	if (askQuit) {
    		client.getMenuTransition().askForStage(null);
    	}
    	
    	// Move the texter
    	if (kbHandler.isKeyDown(Keys.DOWN)) {
    		position = Math.min(texterHeight, position + 1);
    	} else if (kbHandler.isKeyDown(Keys.UP)) {
    		position = Math.max(0,  position - 1);
    	}

	}
}
