package zildo.client.stage;

import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.GUISequence;
import zildo.client.gui.menu.StartMenu;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.input.KeyboardInstant;
import zildo.fwk.ui.UIText;
import zildo.resource.KeysConfiguration;

public class CreditStage extends GameStage {

	GUIDisplay guiDisplay;
	MapDisplay mapDisplay;
	String[] creditText;
	int currentLine;
	int counter;
	KeyboardInstant instant;
	boolean askQuit = false;
	int endCounter=0;
	boolean endScene;
	
	/**
	 * Create a stage displaying credits.
	 * @param p_endScene TRUE means we want the special text for player finishing the game
	 */
	public CreditStage(boolean p_endScene) {
		endScene = p_endScene;
		launchGame();
	}

	@Override
	public void updateGame() {
		counter++;
		if ((counter & 15) == 0) {
			currentLine++;
		}
		// Does user quit ?
        instant.update();
        if (!askQuit) {
        	askQuit = instant.isKeyDown(KeysConfiguration.PLAYERKEY_ACTION);
        	askQuit |= instant.isKeyDown(KeysConfiguration.PLAYERKEY_ATTACK);
        	askQuit |= instant.isKeyDown(KeysConfiguration.PLAYERKEY_DIALOG);
			askQuit |= instant.isKeyDown(KeysConfiguration.PLAYERKEY_BACK);
        	if (askQuit) {
                ClientEngineZildo.filterCommand.fadeOut(FilterEffect.FADE);
        	}
        } else {
        	endCounter++;
    		// Let the fade doing his job
        	if (endCounter > 100) {
        		ClientEngineZildo.getClientForMenu().quitGame();
        		ClientEngineZildo.getClientForMenu().handleMenu(new StartMenu());
        		done = true;
        	}
        }
		
	}

	@Override
	public void renderGame() {
		String sentence = "";
		if (currentLine < creditText.length) {
			sentence = creditText[currentLine];
			guiDisplay.displayCredits(counter, sentence);
		}
		// Scroll is stopped as soon as there's no sentence to display
	}

	@Override
	public void launchGame() {
		guiDisplay = ClientEngineZildo.guiDisplay;
		mapDisplay = ClientEngineZildo.mapDisplay;
		String wholeCredits = UIText.getCreditText("credits");
		if (endScene) {
			wholeCredits = UIText.getCreditText("endscene") + wholeCredits;
		}
		// Analyze text to cut out into lines
		creditText = wholeCredits.split("\n");
		currentLine = 0;
		counter = 0;
		instant = new KeyboardInstant();
	}

	@Override
	public void endGame() {
		ClientEngineZildo.guiDisplay.clearSequences(GUISequence.CREDIT);
	}

}
