/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
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

package zildo.fwk.script.xml;

import org.w3c.dom.Element;

import zildo.monde.items.ItemKind;
import zildo.monde.map.Zone;
import zildo.monde.quest.QuestEvent;

public class TriggerElement extends AnyElement {

	public QuestEvent kind;
	String name;	// dialog, map, item and questDone
	int numSentence;
	Zone region;
	
	public TriggerElement(QuestEvent p_kind) {
		kind=p_kind;
	}
	
	//
	@Override
	public void parse(Element p_elem) {
        if (kind == null) {
        	throw new RuntimeException("Trigger kind is unknown !");
        }
        switch (kind) {
        case DIALOG:
        	numSentence=Integer.valueOf(p_elem.getAttribute("num"));
        case QUESTDONE:
        case LOCATION:
        	name=p_elem.getAttribute("name");
        	break;
        case INVENTORY:
        	name=p_elem.getAttribute("item");
        	break;
        }

	}

	/**
	 * Returns TRUE if the given trigger matches the current one.<p/>
	 * We assume they are same kind, and that given one is undone.
	 * @param p_another
	 * @return boolean
	 */
	public boolean match(TriggerElement p_another) {
		switch (kind) {
			case DIALOG:
				if (p_another.name.equals(name) && p_another.numSentence == numSentence) {
					return true;
				}
				break;
			case INVENTORY:
				if (p_another.name.equals(name)) {
					return true;
				}
				break;
			case LOCATION:
				if (p_another.name.equals(name)) {
					return true;
				}
			case QUESTDONE:
				if (p_another.name.equals(name)) {
					return true;
				}
		}
		return false;
	}
	
	/**
	 * Ingame method to check a dialog trigger.
	 * @param p_name
	 * @param p_num
	 * @return TriggerElement for Dialog
	 */
	public static TriggerElement createDialogTrigger(String p_name, int p_num) {
		TriggerElement elem=new TriggerElement(QuestEvent.DIALOG);
		elem.name=p_name;
		elem.numSentence=p_num;
		return elem;
	}
	
	/**
	 * Ingame method to check a inventory trigger.
	 * @param p_name
	 * @return TriggerElement for Inventory
	 */
	public static TriggerElement createInventoryTrigger(ItemKind p_item) {
		TriggerElement elem=new TriggerElement(QuestEvent.INVENTORY);
		elem.name=p_item.toString();
		return elem;
	}
	
	/**
	 * Ingame method to check a location trigger.
	 * @param p_mapName
	 * @return TriggerElement
	 */
	public static TriggerElement createLocationTrigger(String p_mapName) {
		TriggerElement elem=new TriggerElement(QuestEvent.LOCATION);
		elem.name=p_mapName;
		return elem;
	}
	
	/**
	 * Ingame method to check a 'quest done' trigger.
	 * @param p_quest
	 * @return TriggerElement
	 */
	public static TriggerElement createQuestDoneTrigger(String p_quest) {
		TriggerElement elem=new TriggerElement(QuestEvent.QUESTDONE);
		elem.name=p_quest;
		return elem;
	}
}