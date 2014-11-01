/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zildo.monde.quest.actions;

import java.util.ArrayList;
import java.util.List;

import zildo.monde.dialog.ActionDialog;
import zildo.monde.items.Inventory;
import zildo.monde.items.StoredItem;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.server.EngineZildo;
import zildo.server.state.ClientState;

/**
 * Action launched in order to make Zildo buy items with a given character.
 * @author Tchegito
 *
 */
public class BuyingAction extends ActionDialog {

	PersoPlayer zildo;
	Perso seller;
	List<StoredItem> sellingItems;
	String sellDescription;
	
	/**
	 * @param p_text
	 */
	public BuyingAction(String p_text) {
		super(p_text);
	}

	public BuyingAction(PersoPlayer p_zildo, Perso p_seller, String p_sellDescription) {
		super(null);
		zildo = p_zildo;
		seller = p_seller;
		String itemsAsString = EngineZildo.scriptManagement.getVarValue(p_sellDescription);
		sellingItems = StoredItem.fromString(itemsAsString); 
		sellDescription = p_sellDescription;
	}
	
	@Override
	public void launchAction(ClientState p_clientState) {
		
		zildo.setDialoguingWith(seller);
		seller.setDialoguingWith(zildo);
		
		List<StoredItem> items=new ArrayList<StoredItem>();
		for (StoredItem sItem : sellingItems) {
			if (sItem.quantity != 0) {
				items.add(sItem);
			}
		}
		zildo.lookItems(new Inventory(items), 0, seller, sellDescription);
		
		p_clientState.dialogState.dialoguing=true;
	}

}
