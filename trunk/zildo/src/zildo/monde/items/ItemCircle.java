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

package zildo.monde.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.client.gui.GUISpriteSequence;
import zildo.client.sound.BankSound;
import zildo.fwk.ZUtils;
import zildo.fwk.bank.SpriteBank;
import zildo.fwk.gfx.EngineFX;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.monde.dialog.WaitingDialog;
import zildo.monde.dialog.WaitingDialog.CommandDialog;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.desc.FontDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoPlayer;
import zildo.monde.util.Point;
import zildo.server.EngineZildo;
import zildo.server.Server;
import zildo.server.state.ClientState;

public class ItemCircle {

	public enum CirclePhase {
		EXPANSION, FIXED, REDUCTION, ROTATE_LEFT, ROTATE_RIGHT; 
		
		public boolean isSizeChanging() {
			return this==EXPANSION || this==REDUCTION;
		}
		public boolean isRotating() {
			return this==ROTATE_LEFT || this==ROTATE_RIGHT;
		}
	}
	
	private GUISpriteSequence seq;
	private List<SpriteEntity> guiSprites;
	private List<StoredItem> items;
	private Point center;
	private CirclePhase phase;	// 0=create 1=fixed 2=remove 3=scroll
	private int itemSelected;	// From 0 to guiSprites.size()-1
	private int count;
	private Perso perso;	// The one at the center of the circle
	private PersoPlayer client;	// Define the Zildo acting
	private boolean describe;	// TRUE=Describe selected item in dialog area
	
	/**
	 * Create an ItemCircle object, related to a given client, identified by the PersoZildo object.
	 * @param p_zildoClient
	 */
	public ItemCircle(PersoPlayer p_zildoClient) {
		guiSprites=new ArrayList<SpriteEntity>();
		count=0;
		itemSelected=0;
		phase=CirclePhase.EXPANSION;
		client=p_zildoClient;
		seq = new GUISpriteSequence();
	}
	
	public List<SpriteEntity> getSprites() {
		return guiSprites;
	}
	
	/**
	 * Create the circle with given items
	 * @param p_items
	 * @param p_heros Character who is the center of the item circle
	 * @param p_buying TRUE=This circle is a store inventory, and hero can buy some.
	 * @param p_x
	 * @param p_y
	 */
	public void create(Inventory p_inventory, int p_selected, Perso p_heros, boolean p_buying) {
		count=0;
		itemSelected=p_selected;
		guiSprites.clear();
		perso=p_heros;
		describe=p_buying;
		items=p_inventory.items;
		
		center = new Point(perso.getScrX()+1, perso.getScrY()+4);
		
		for (StoredItem item : p_inventory.items) {
            SpriteEntity e = seq.addSprite(item.item.kind.representation, 
            		center.x, center.y);
            e.setSpecialEffect(EngineFX.FOCUSED);
            e.zoom = 0;
            guiSprites.add(e);

            int xx = 0;
            byte[] quantityBase10 = ZUtils.decomposeBase10(item.quantity);
            for (int i=0;i<2;i++) {
            	int digit = 0;
            	if (quantityBase10.length > i) {
            		digit = quantityBase10[i];
            	}
	            e = seq.addSprite(SpriteBank.BANK_FONTES, FontDescription.N_0.getNSpr() + digit,
	            		center.x + xx, center.y, true, 255);
	            e.setSpecialEffect(EngineFX.FOCUSED);
	            e.setVisible(item.quantity > 1);
	            xx+=8;
            }

		}
		display();
		
		// Focused buyer and seller (or just hero), and launch a semi-fade out
		perso.setSpecialEffect(EngineFX.FOCUSED);
		client.setSpecialEffect(EngineFX.FOCUSED);
		EngineZildo.soundManagement.playSound(BankSound.MenuOut, client);
		EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_OUT, FilterEffect.SEMIFADE));
	}
	
	/**
	 * Place the entity to have the circle around the center, regards to circle's phase.
	 */
	private void display() {
		
		int rayon=32;
		double alpha=0;
		double pas=2*Math.PI / guiSprites.size();
		if (phase.isSizeChanging()) {
			rayon=count;
		} else if (phase.isRotating()) {
			double diff=(pas * count / 32);
			if (phase==CirclePhase.ROTATE_RIGHT) {
				diff=-diff;
			}
			alpha+=diff;
		}
		alpha-=pas*itemSelected;
		// Create the inventory sprites
		Iterator<SpriteEntity> itSeq = seq.iterator();
		
		if (itSeq.hasNext()) {
			for (StoredItem item : items) {
	            SpriteEntity entity = itSeq.next();
				int itemX=(int) (rayon*Math.sin(alpha));
				int itemY=(int) (-rayon*Math.cos(alpha));
				SpriteModel model = entity.getSprModel();
				entity.setScrX(center.x + itemX);
				entity.setScrY(center.y + itemY);
				entity.zoom = Math.min(255, rayon * 8); //16; //255 * (33 / (33-rayon));
	
				if (Math.abs(alpha % 2*Math.PI) < 0.01) {	// Highlight the upper item
					entity.setSpecialEffect(EngineFX.YELLOW_HALO);
				} else {
					entity.setSpecialEffect(EngineFX.NO_EFFECT);
				}
                byte[] quantityBase10 = ZUtils.decomposeBase10(item.quantity);
                
                int xx= 0;
                for (int i=0;i<2;i++) {
                    SpriteEntity amount = itSeq.next();
                	if (quantityBase10.length > i) {
		            	// Display the amount number just on the right of the item
                		int digit = quantityBase10[i];
                		amount.setNSpr(FontDescription.N_0.getNSpr() + digit);
                		EngineZildo.spriteManagement.updateSprModel(amount);
		                amount.setScrX(center.x + xx + itemX + model.getTaille_x());
		                amount.setScrY(center.y + itemY);
		                amount.zoom = entity.zoom;
		                xx += 8;
		            } else {
		            	amount.visible = false;
		            }
                }
				alpha+=pas;
			}
		}
	}
	
	/**
	 * Main method for caller.
	 */
	public void animate() {
		switch (phase) {
		case EXPANSION:
		case ROTATE_LEFT:
		case ROTATE_RIGHT:
			if (count < 32) {
				count+=2;
			} else {
				if (phase==CirclePhase.ROTATE_LEFT) {
					itemSelected= (itemSelected+guiSprites.size() -1) % guiSprites.size();
					EngineZildo.soundManagement.playSound(BankSound.MenuMove, client);
				} else if (phase==CirclePhase.ROTATE_RIGHT) {
					itemSelected= (itemSelected +1) % guiSprites.size();
					EngineZildo.soundManagement.playSound(BankSound.MenuMove, client);
				}
				phase=CirclePhase.FIXED;
				
				if (describe) {
					displayName();
				}
			}
			break;
		case REDUCTION:
			if (count > 0) {
				count-=2;
			} else {
				phase=CirclePhase.FIXED;
				kill();
			}
			break;
		}
		display();
	}
	
	private void displayName() {
		StoredItem item = items.get(itemSelected);
		ClientState clState = Server.getClientFromZildo(client);
		EngineZildo.dialogManagement.getQueue().add(new WaitingDialog(null, item.getName(), CommandDialog.BUYING, false, clState == null ? null : clState.location));
	}
	
	public void rotate(boolean p_clockWise) {
		if (!phase.isRotating()) {
			if (p_clockWise) {
				phase=CirclePhase.ROTATE_LEFT;
			} else {
				phase=CirclePhase.ROTATE_RIGHT;
			}
			count=0;
		}
	}
	
	public boolean isAvailable() {
		return phase==CirclePhase.FIXED;
	}
	
	public boolean isReduced() {
		return guiSprites.size() == 0;
	}
	
	/**
	 * Soft remove circle.
	 */
	public void close() {
		phase=CirclePhase.REDUCTION;
		EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.SEMIFADE));
	}
	
	public StoredItem getItemSelected() {
		return items.get(itemSelected);
	}
	
	/**
	 * Decrements quantity of selected item.
	 */
	public void decrementSelected() {
		items.get(itemSelected).decrements();
		if (items.get(itemSelected).quantity == 0) {
			// Remove it from the list
			items.remove(itemSelected);
			guiSprites.remove(itemSelected);
			seq.remove(itemSelected*2);
			seq.remove(itemSelected*2);
			if (items.size() == 0) {
				kill();
			} else if (itemSelected == items.size()) {
				itemSelected--;
			}
		}
	}
	
	/**
	 * Emergency remove circle.
	 */
	public void kill() {
		for (SpriteEntity e : guiSprites) {
			e.dying=true;
		}
		guiSprites.clear();
		seq.clear();

		// Unfocus involved persos
		client.initPersoFX();
		perso.initPersoFX();
		EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.SEMIFADE));

	}
}
