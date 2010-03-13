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

package zildo.client.gui.menu;

import zildo.client.ClientEngineZildo;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.ui.EditableItemMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.prefs.Constantes;

/**
 * @author eboussaton
 */
public class PlayerNameMenu extends Menu {

    public PlayerNameMenu(final StringBuilder p_playerName, Menu p_previous) {
        super("m5.title");

        previousMenu = p_previous;

        ItemMenu itemName = new EditableItemMenu(p_playerName) {
            @Override
            public void run() {
            	// Save playername on disk
            	savePlayerName(p_playerName.toString());
            	// Back to previous menu
                ClientEngineZildo.getClientForMenu().handleMenu(previousMenu);
            }
        };

        setMenu(itemName);
    }
    
    /**
     * Save the player name in the configuration file {@link Constantes#CONFIGURATION_FILE}
     * @param p_playerName
     */
    private void savePlayerName(String p_playerName) {
    	EasyBuffering buffer=new EasyBuffering(p_playerName.length()*2);
    	buffer.put(p_playerName);
    	EasyWritingFile file=new EasyWritingFile(buffer);
    	file.saveFile(Constantes.CONFIGURATION_FILE);
    }
    
    /**
     * Read the player configuration file to return the chosen name.<br/>
     * In any error case, return default <b>Zildo</b>, which is definitely a good name.
     * @return String
     */
    static public String loadPlayerName() {
    	try {
        	EasyReadingFile file=new EasyReadingFile(Constantes.CONFIGURATION_FILE);
    		return file.readString();
    	} catch (Exception e) {
    		return "Zildo";
    	}
    }
}