package zildo.client.gui.menu;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.dialog.HistoryRecord;
import zildo.server.EngineZildo;

public class CompassMenu extends Menu {

	public CompassMenu() {
		items = new ArrayList<ItemMenu>();
		items.add(new ItemMenu("m13.map") {
			@Override
			public void run() {
			}
		});
		items.add(new ItemMenu("m13.dial") {
			
			@Override
			public void run() {
				List<HistoryRecord> records = EngineZildo.game.getLastDialog();
				System.out.println(HistoryRecord.getDisplayString(records));
				
				client.handleMenu(null);
			}
		});
		items.add(new ItemMenu("m13.guide") {
			@Override
			public void run() {
			}
		});
		setMenu(items);
	}
}
