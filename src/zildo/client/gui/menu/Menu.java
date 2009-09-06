package zildo.client.gui.menu;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import zildo.client.ClientEngineZildo;
import zildo.client.SoundPlay.BankSound;

public class Menu {

	public String title;
	public List<ItemMenu> items;
	public int selected;
	public boolean displayed;

	private static int keyPressed;
	
	public Menu() {
		
	}
	public Menu(String p_title, ItemMenu... p_items) {
		title=p_title;
		setMenu(p_items);
	}
	
	public void setMenu(ItemMenu... p_items) {
		items=Arrays.asList(p_items);
		displayed=false;
	}
	
	public void setTitle(String p_title) {
		title=p_title;
	}
	
	/**
	 * @param p_direction FALSE=UP / TRUE=DOWN
	 */
	public void move(boolean p_direction) {
		int add=p_direction ? 1 : items.size()-1;
		selected=(selected+add) % items.size();
		ClientEngineZildo.soundPlay.playSoundFX(BankSound.MenuMove);
	}
	
	public ItemMenu act() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if (keyPressed!=Keyboard.KEY_UP) {
				move(false);
				keyPressed=Keyboard.KEY_UP;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (keyPressed!=Keyboard.KEY_DOWN) { 
				move(true);
				keyPressed=Keyboard.KEY_DOWN;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			if (keyPressed!=Keyboard.KEY_RETURN) {
				keyPressed=Keyboard.KEY_RETURN;
				ItemMenu item=items.get(selected);
				ClientEngineZildo.soundPlay.playSoundFX(item.sound);
				return item;
			}
		} else {
			keyPressed=0;
		}
		return null;
	}
}
