package zildo.client.stage;

import java.util.List;

import zildo.Zildo;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUISequence;
import zildo.client.gui.GUIDisplay.DialogMode;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;
import zildo.monde.dialog.HistoryRecord;
import zildo.server.EngineZildo;

public class TexterStage extends GameStage {
	
	String wholeText;
	KeyboardHandler kbHandler;
	
	public TexterStage() {
		List<HistoryRecord> records = EngineZildo.game.getLastDialog();
		wholeText = HistoryRecord.getDisplayString(records);
		kbHandler = Zildo.pdPlugin.kbHandler;
	}
	
	@Override
	public void launchGame() {

	}
	
	@Override
	public void renderGame() {
		if (!done) {
			ClientEngineZildo.guiDisplay.displayTexter(wholeText);
		}
	}
	
	@Override
	public void endGame() {
		ClientEngineZildo.guiDisplay.clearSequences(GUISequence.CREDIT, GUISequence.FRAME_DIALOG);
		ClientEngineZildo.guiDisplay.setToDisplay_dialogMode(DialogMode.CLASSIC);
	}
	
	@Override
	public void updateGame() {
    	boolean askQuit = kbHandler.isKeyPressed(Keys.ESCAPE);
    	askQuit |= kbHandler.isKeyPressed(Keys.COMPASS);
    	if (askQuit) {
    		endGame();
    		done = true;
    	}
	}
}
