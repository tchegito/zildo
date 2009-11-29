package zildo.monde.quest.actions;

import zildo.SinglePlayer;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.menu.StartMenu;
import zildo.monde.dialog.ActionDialog;
import zildo.server.EngineZildo;

/**
 * When Zildo dies in single player.
 * <p/>
 * Displays a message and shut down the single player.<br/>
 * Back to the start menu.
 * 
 * @author tchegito
 *
 */
public class GameOverAction extends ActionDialog {

	public GameOverAction() {
		super("Game over ! Merci d'avoir joue a la legende de Zildo.");
	}
	
	@Override
	public void launchAction() {
		// Reset map / GUI
		EngineZildo.mapManagement.deleteCurrentMap();
		ClientEngineZildo.tileEngine.cleanUp();
		ClientEngineZildo.guiDisplay.setToDisplay_generalGui(false);
		// Stop this game
		SinglePlayer.getClientState().gameOver=true;
		// Return on the start menu
		ClientEngineZildo.getClientForMenu().handleMenu(new StartMenu());
	}

}
