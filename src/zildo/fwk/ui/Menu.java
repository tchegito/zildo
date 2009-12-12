package zildo.fwk.ui;

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
	public Menu previousMenu;
	
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
	
	/**
	 * Read keyboard, move cursor, and return chosen item. Some items can be editable.
	 * @return ItemMenu
	 */
    public ItemMenu act() {
        Keyboard.poll();
        int key = -1;
        char charKey=' ';
        char upperKey=' ';
        ItemMenu item;
        while (Keyboard.next()) {
        	if (Keyboard.getEventKeyState()) {
	            key = Keyboard.getEventKey(); //Keyboard.getEventCharacter();
	            charKey = Keyboard.getEventCharacter();
	            upperKey = Character.toUpperCase(charKey);
        	}
        }
        
        if (key != -1 && keyPressed != key) {
            item = items.get(selected);

        	switch (key) {
        	case Keyboard.KEY_UP:
                move(false);
        		break;
        	case Keyboard.KEY_DOWN:
                move(true);
        		break;
        	case Keyboard.KEY_RETURN:
                ClientEngineZildo.soundPlay.playSoundFX(item.sound);
                return item;
            default:
            	// Does this item is editable ?
	        	if (item instanceof EditableItemMenu) {
	        		EditableItemMenu editableItem=(EditableItemMenu) item;
	        		switch (key) {
		        	case Keyboard.KEY_BACK:
		                editableItem.removeLastChar();
		                displayed=false;
		                break;
		        	default:
		        		if (EditableItemMenu.acceptableChar.indexOf(upperKey) != -1) {
		                    editableItem.addText(charKey);
		                    displayed = false;
		        		}
		        		break;
		        	}
		        }
        	}
        	keyPressed=key;
        } else {
        	keyPressed=0;
        }
        return null;
    }
    
    public void refresh() {
    
    }
    
    public Menu getPrevious() {
    	return previousMenu;
    }
}
