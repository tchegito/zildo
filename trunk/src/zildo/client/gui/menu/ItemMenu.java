package zildo.client.gui.menu;

import zildo.client.SoundPlay.BankSound;

public abstract class ItemMenu {

	public String text;
	public BankSound sound=BankSound.MenuSelect;
	
	public ItemMenu(String p_text) {
		text=p_text;
	}

	public ItemMenu(String p_text, BankSound p_sound) {
		text=p_text;
		sound=p_sound;
	}

	public abstract void run();
		
}
