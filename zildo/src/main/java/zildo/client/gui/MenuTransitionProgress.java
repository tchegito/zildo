package zildo.client.gui;

import zildo.client.Client;
import zildo.client.stage.GameStage;
import zildo.client.stage.MenuStage;
import zildo.fwk.ui.Menu;


/** Class handling smooth transition from/to a menu.
 * If a menu should come, or disappear, it will fade in a defined number of frame.
 * If a new stage is awaited, we wait for the current menu to disappear, and then we invoke the new stage
 * @author Tchegito
 *
 */
public class MenuTransitionProgress {
	
	// Decreasing number of frames during user can't interact
	int framesAwaiting;

	Menu currentMenu;
	Menu nextMenu;
	GameStage nextStage;
	GameStage currentStage;	// TODO: see if we can remove currentMenu and currentStage (maybe merge them) to replace by a boolean which says "I'm in a stage faded in, waiting user action to fade out"
	Client client;
	
	boolean fadingOut = false;
	
	public static final int BLOCKING_FRAMES_ON_MENU_INTERACTION = 10;
	
	public MenuTransitionProgress(Client client) {
		this.client = client;
	}
	
	public void askForMenu(Menu menu) {
		nextMenu = menu;
		// If we haven't any menu for now, then make the new one fading in
		if (currentMenu == null) {
			fadingOut = false;
			if (nextMenu == null) return;
			activateNextSequence();
		} else {
			fadingOut = true;
		}
		framesAwaiting = BLOCKING_FRAMES_ON_MENU_INTERACTION;
	}
	
	public void askForStage(GameStage stage) {
		askForMenu(null);
		if (stage == null && currentStage != null) {
			fadingOut = true;
			framesAwaiting = BLOCKING_FRAMES_ON_MENU_INTERACTION;
		}
		nextStage = stage;
	}
	
	public void forceMenu(Menu menu) {
		currentMenu = menu;
	}
	
	public boolean isReadyToInteract() {
		if (framesAwaiting == 0) {
			return true;
		}
		return false;
	}
	
	public boolean isCurrentOver() {
		return framesAwaiting == 1 && fadingOut;
	}
	
	private void activateNextSequence() {
		// Switch from currentMenu to nextMenu

		if (nextMenu != null) {
			client.askStage(new MenuStage(nextMenu));
			nextMenu.displayed = false;
			framesAwaiting = BLOCKING_FRAMES_ON_MENU_INTERACTION;
		}
		//fadingOut = false;
		currentMenu = nextMenu;
		nextMenu = null;
		
		// Ask for the new stage
		if (nextStage != null) {
			client.askStage(nextStage);
			// Next lines may not be necessary for singleplayer stage
			framesAwaiting = BLOCKING_FRAMES_ON_MENU_INTERACTION;

			//System.out.println("set stage to "+nextStage);
			currentStage = nextStage;
			nextStage = null;
		}
		fadingOut = false;
	}
	
	public void mainLoop() {
		if (framesAwaiting == 1) {
			if (fadingOut) {
				activateNextSequence();
			}
		}
		
		framesAwaiting = Math.max(0,  framesAwaiting-1);
		
		
	}
	
	/** Returns 0..255 integer**/
	public int getFadeLevel() {
		if (framesAwaiting == 0) {
			return currentMenu == null ? 0 : 255;
		} else {
			int val = (int) (255 * (framesAwaiting / (float) BLOCKING_FRAMES_ON_MENU_INTERACTION));
			if (fadingOut) {
				return val;
			} else {
				return 255-val;
			}
		}
	}
	
	public Menu getCurrentMenu() {
		return currentMenu;
	}
}