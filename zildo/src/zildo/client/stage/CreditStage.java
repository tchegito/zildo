package zildo.client.stage;

import zildo.client.ClientEngineZildo;
import zildo.client.MapDisplay;
import zildo.client.gui.GUIDisplay;
import zildo.fwk.ui.UIText;

public class CreditStage implements GameStage {

	GUIDisplay guiDisplay;
	MapDisplay mapDisplay;
	String[] creditText;
	int currentLine;
	int counter;
	
	public CreditStage() {
		launchGame();
	}

	@Override
	public void updateGame() {
		counter++;
		if ((counter & 15) == 0) {
			currentLine++;
		}
	}

	@Override
	public void renderGame() {
		String sentence = "";
		if (currentLine < creditText.length) {
			sentence = creditText[currentLine];
		}
		guiDisplay.displayCredits(counter, sentence);
	}

	@Override
	public void launchGame() {
		guiDisplay = ClientEngineZildo.guiDisplay;
		mapDisplay = ClientEngineZildo.mapDisplay;
		String wholeCredits = UIText.getCreditText("credits");
		// Analyze text to cut out into lines
		creditText = wholeCredits.split("\n");
		currentLine = 0;
		counter = 0;
	}

	@Override
	public void endGame() {

	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
