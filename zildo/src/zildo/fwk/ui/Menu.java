/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk.ui;

import java.util.Arrays;
import java.util.List;

import zildo.Zildo;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.BankSound;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;

public class Menu {

	public String title;
	public List<ItemMenu> items;
	protected int selected;
	public boolean displayed;
	public Menu previousMenu;
	
	private static int keyPressed;
	
	// Object to handle any menus
	protected Client client = ClientEngineZildo.getClientForMenu();
	protected final Menu currentMenu = this;
	protected String keyText;
	
	public Menu() {

	}
	
	public Menu(String p_title, ItemMenu... p_items) {
		setTitle(p_title);
		setMenu(p_items);
	}
	
	public void setMenu(ItemMenu... p_items) {
		items=Arrays.asList(p_items);
		displayed=false;
		init();
	}

	public void setMenu(List<ItemMenu> p_items) {
		items=p_items;
		displayed=false;
		init();
	}
	
	public void setTitle(String p_title) {
		keyText = p_title;
		title=UIText.getMenuText(p_title);
	}
	
	protected void init() {
		// Find the first selectable item
		selected = 0;
		for (ItemMenu i : items) {
			if (!i.isSelectable()) {
				selected++;
			} else {
				break;
			}
		}	
	}

	/**
	 * @param p_direction FALSE=UP / TRUE=DOWN
	 */
	public void move(boolean p_direction) {
		int add=p_direction ? 1 : items.size()-1;
		while(true) {
			selected=(selected+add) % items.size();
			ItemMenu item = items.get(selected);
			if (item.isSelectable()) {
				break;
			}
		}
		ClientEngineZildo.soundPlay.playSoundFX(BankSound.MenuMove);
	}
	
	/**
	 * Read keyboard, move cursor, and return chosen item. Some items can be editable.
	 * @return ItemMenu
	 */
    public ItemMenu act() {
        int key = -1;
        char charKey=' ';
        char upperKey=' ';
        ItemMenu item;
        KeyboardHandler kbHandler = Zildo.pdPlugin.kbHandler;
        while (kbHandler.next()) {
        	if (kbHandler.getEventKeyState()) {
	            key = kbHandler.getEventKey(); //Keyboard.getEventCharacter();
	            charKey = kbHandler.getEventCharacter();
	            upperKey = Character.toUpperCase(charKey);
        	}
        }
        
        if (key != -1 && keyPressed != key) {
            item = items.get(selected);

        	if (key == kbHandler.getCode(Keys.UP)) {
                move(false);
        	} else if (key == kbHandler.getCode(Keys.DOWN)) {
                move(true);
        	} else if (key == kbHandler.getCode(Keys.RETURN)) {
                return item;
        	} else {
            	// Does this item is editable ?
	        	if (item instanceof EditableItemMenu) {
	        		EditableItemMenu editableItem=(EditableItemMenu) item;
	        		if (key == kbHandler.getCode(Keys.BACK)) {
		                editableItem.removeLastChar();
		                displayed=false;
	        		} else {
		        		if (EditableItemMenu.acceptableChar.indexOf(upperKey) != -1) {
		                    editableItem.addText(charKey);
		                    displayed = false;
		        		}
		        	}
		        }
        	}
        	keyPressed=key;
        } else {
        	keyPressed=0;
        }
        return null;
    }
    
    /**
     * Used with touchscreen platform.
     * @param item
     */
    public void selectItem(ItemMenu item) {
		int pos = items.indexOf(item);
    	if (selected != pos && item.isSelectable()) {
    		selected=pos;
    		ClientEngineZildo.soundPlay.playSoundFX(BankSound.MenuMove);
    	}
    }
    
    public void refresh() {
    
    }
    
    public Menu getPrevious() {
    	return previousMenu;
    }
    
    /**
     * Retrieve item from the menu whose bundle key is the given name.
     * @param name
     * @return ItemMenu (NULL if no such key can be found)
     */
    public ItemMenu getItemNamed(String name) {
    	if (name == null) {
    		return null;
    	}
    	for (ItemMenu item : items) {
    		if (name.equals(item.getKey())) {
    			return item;
    		}
    	}
    	return null;	// No item found with the given name
    }
    
    public String getKey() {
    	return keyText;
    }
    
    public int getSelected() {
    	return selected;
    }
}
