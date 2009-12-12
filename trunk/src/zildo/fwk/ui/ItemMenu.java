package zildo.fwk.ui;

import zildo.client.SoundPlay.BankSound;

public abstract class ItemMenu {

	private  String text;
	public BankSound sound=BankSound.MenuSelect;
	
	public ItemMenu() {
		
	}
	
	public ItemMenu(String p_text) {
		text=UIText.getText(p_text);
	}

	public ItemMenu(String p_text, BankSound p_sound) {
		this(p_text);
		sound=p_sound;
	}

	public String getText() {
		return text;
	}
	
	/**
	 * Set an item name without bundle
	 * @param p_text
	 */
	public void setText(String p_text) {
		text=p_text;
	}
	
	public abstract void run();
		
}
