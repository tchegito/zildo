/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
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

import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.client.sound.BankSound;
import zildo.fwk.input.KeyboardHandler;

public class Menu {

	public String title;
	public List<ItemMenu> items;
	public int selected;
	public boolean displayed;
	public Menu previousMenu;
	
	private static int keyPressed;
	
	// Object to handle any menus
	protected Client client = ClientEngineZildo.getClientForMenu();
	protected final Menu currentMenu = this;
	
	public Menu() {
		
	}
	
	public Menu(String p_title, ItemMenu... p_items) {
		title=UIText.getMenuText(p_title);
		setMenu(p_items);
	}
	
	public void setMenu(ItemMenu... p_items) {
		items=Arrays.asList(p_items);
		displayed=false;
	}
	public void setMenu(List<ItemMenu> p_items) {
		items=p_items;
		displayed=false;
	}
	
	public void setTitle(String p_title) {
		title=UIText.getMenuText(p_title);
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
        int key = -1;
        char charKey=' ';
        char upperKey=' ';
        ItemMenu item;
        while (KeyboardHandler.next()) {
        	if (KeyboardHandler.getEventKeyState()) {
	            key = KeyboardHandler.getEventKey(); //Keyboard.getEventCharacter();
	            charKey = KeyboardHandler.getEventCharacter();
	            upperKey = Character.toUpperCase(charKey);
        	}
        }
        
        if (key != -1 && keyPressed != key) {
            item = items.get(selected);

        	switch (key) {
        	case KeyboardHandler.KEY_UP:
                move(false);
        		break;
        	case KeyboardHandler.KEY_DOWN:
                move(true);
        		break;
        	case KeyboardHandler.KEY_RETURN:
                ClientEngineZildo.soundPlay.playSoundFX(item.sound);
                item.setLaunched(false);
                return item;
            default:
            	// Does this item is editable ?
	        	if (item instanceof EditableItemMenu) {
	        		EditableItemMenu editableItem=(EditableItemMenu) item;
	        		switch (key) {
		        	case KeyboardHandler.KEY_BACK:
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
