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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tchegito
 *
 */
public class PageableMenu extends Menu {

	PageableMenu currentPageableMenu = this;
	
	List<ItemMenu> completeItems;
	int currentPage;
	
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
			initCurrentPage();
		}
	}
	
	private void initCurrentPage() {
		items=new ArrayList<ItemMenu>();
		int indexStart = currentPage * NB_PER_PAGE;
		int indexEnd = Math.min(indexStart + NB_PER_PAGE, completeItems.size());
		items.addAll(completeItems.subList(indexStart, indexEnd));
		// 'Next' button, if we have too much items on this page
		if (indexEnd < completeItems.size()) {
			items.add(new ItemMenu("global.next") {
				@Override
				public void run() {
					currentPage++;
					initCurrentPage();
					selected = 0;
					client.handleMenu(currentPageableMenu);
				}
			});
		}
		// 'Prec' button, if we aren't on the first page
		if (currentPage > 0) {
			items.add(new ItemMenu("global.prec") {
				@Override
				public void run() {
					currentPage--;
					initCurrentPage();
					selected = 0;
					client.handleMenu(currentPageableMenu);
				}
			});
		}
	}
}
