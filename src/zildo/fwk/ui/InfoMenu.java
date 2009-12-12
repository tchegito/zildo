package zildo.fwk.ui;

import zildo.client.ClientEngineZildo;

public class InfoMenu extends Menu {

	public InfoMenu(String p_message, final Menu p_next) {
		this(p_message, "Ok", p_next);
	}
	
	public InfoMenu(String p_message, String p_itemText, final Menu p_next) {
		super(p_message);
		
		ItemMenu itemOk=new ItemMenu(p_itemText) {
			public void run() {
				ClientEngineZildo.getClientForMenu().handleMenu(p_next);
			}
		};
		
        setMenu(itemOk);
	}
}
