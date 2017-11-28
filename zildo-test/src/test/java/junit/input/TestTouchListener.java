package junit.input;

import org.junit.Test;

import tools.EngineUT;
import zildo.client.ClientEngineZildo;
import zildo.client.gui.GUIDisplay;
import zildo.client.gui.menu.SaveGameMenu;
import zildo.fwk.ZUtils;

public class TestTouchListener extends EngineUT {

	
	@Test
	public void concurrentModification() {
		GUIDisplay gui = ClientEngineZildo.guiDisplay;
		new Thread() {
			@Override
			public void run() {
				System.out.println("go !");
				for (int i=0;i<100;i++) {
					System.out.println("get");
					gui.getItemOnLocation(160, 100);
					ZUtils.sleep(1);
				}
			}
		}.start();
		gui.displayMenu(new SaveGameMenu(false, null));
		
	}
}
