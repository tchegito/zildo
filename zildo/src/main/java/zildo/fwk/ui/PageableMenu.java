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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zildo.Zildo;
import zildo.fwk.input.KeyboardHandler;
import zildo.fwk.input.KeyboardHandler.Keys;

/**
 * @author Tchegito
 *
 * On PC version, player can press PAGEUP/PAGEDOWN to switch between pages.
 */
public class PageableMenu extends Menu {

	PageableMenu currentPageableMenu = this;
	
	List<ItemMenu> completeItems;
	int currentPage;
	int maxPage;
	
	ItemMenu itemPreviousPage;
	ItemMenu itemNextPage;
	
	final static int NB_PER_PAGE = 10;
	
	public PageableMenu(String p_title) {
		super(p_title);
	}
	
	@Override
	public void setMenu(ItemMenu... p_items) {
		completeItems=Arrays.asList(p_items);
		displayed=false;
		
		if (p_items.length > NB_PER_PAGE) {
			// Too much items => get a sublist, and add a 'next page' item
			currentPage = 0;
			maxPage = completeItems.size() / NB_PER_PAGE;
			initCurrentPage();
			
			// Display number of items in title
			super.setTitle(title + " ("+p_items.length+")");
		} else if (items != null) { //if (p_items.length > 0){
			// Back button
			items.add(new ItemMenu("global.back") {
				@Override
				public void run() {
					client.handleMenu(previousMenu);
				}
			});
			init();
		}
	}
	
	private void initCurrentPage() {
		items=new ArrayList<>();
		int indexStart = currentPage * NB_PER_PAGE;
		int indexEnd = Math.min(indexStart + NB_PER_PAGE, completeItems.size());
		items.addAll(completeItems.subList(indexStart, indexEnd));
		// 'Next' button, if we have too much items on this page
		itemNextPage = null;
		if (indexEnd < completeItems.size()) {
			itemNextPage = new ItemMenu("global.next") {
				@Override
				public void run() {
					nextPage();
				}
			};
			items.add(itemNextPage);
		}
		// 'Prec' button, if we aren't on the first page
		itemPreviousPage = null;
		if (currentPage > 0) {
			itemPreviousPage = new ItemMenu("global.prec") {
				@Override
				public void run() {
					previousPage();
				}
			};
			items.add(itemPreviousPage);
		}
		
		// Back button
		items.add(new ItemMenu("global.back") {
			@Override
			public void run() {
				client.handleMenu(previousMenu);
			}
		});
		
		init();

	}
    
    protected ItemMenu handleKey(ItemMenu item, int key) {
        KeyboardHandler kbHandler = Zildo.pdPlugin.kbHandler;
    	if (key == kbHandler.getCode(Keys.PAGEUP) && itemPreviousPage != null) {
			return itemPreviousPage;
		} else if (key == kbHandler.getCode(Keys.PAGEDOWN)) {
			return itemNextPage;
		}
    	return super.handleKey(item, key);
    }
    
	private void changePage() {
		initCurrentPage();
		client.handleMenu(currentPageableMenu);
		
	}
	private void nextPage() {
		currentPage = Math.min(maxPage, currentPage + 1);
		changePage();
	}
	
	private void previousPage() {
		currentPage = Math.max(0, currentPage - 1);
		changePage();
	}
}
