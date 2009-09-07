package zildo.client.gui.menu;

import zildo.client.ClientEngineZildo;

public class InfoMenu extends Menu {

	public InfoMenu(String p_message, final Menu p_next) {
		super(p_message);
		
		ItemMenu itemOk=new ItemMenu("Ok") {
			public void run() {
				ClientEngineZildo.getClientForMenu().handleMenu(p_next);
			}
		};
		
        setMenu(itemOk);
	}
}
