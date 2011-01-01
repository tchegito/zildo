/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zildo.SinglePlayer;
import zildo.client.Client;
import zildo.client.ClientEngineZildo;
import zildo.fwk.file.EasyBuffering;
import zildo.fwk.file.EasyReadingFile;
import zildo.fwk.file.EasyWritingFile;
import zildo.fwk.ui.InfoMenu;
import zildo.fwk.ui.ItemMenu;
import zildo.fwk.ui.Menu;
import zildo.monde.Game;
import zildo.prefs.Constantes;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class SaveGameMenu extends Menu {

	Client client = ClientEngineZildo.getClientForMenu();

	final boolean load;

	/**
	 * Constructor for save game menu.
	 * 
	 * @param p_savegames
	 * @param p_load
	 *            TRUE=client want to load / FALSE=save
	 * @param p_previous
	 */
	public SaveGameMenu(boolean p_load, Menu p_previous) {
		super("m8.title");

		previousMenu = p_previous;
		load = p_load;

		final List<String> savegames = SaveGameMenu.findSavegame();
		List<ItemMenu> items = new ArrayList<ItemMenu>();
		for (final String s : savegames) {
			items.add(new ItemMenu(s) {
				@Override
				public void run() {
					int number = getSavegameNumber(s);
					String filename = getSavegameFilename(number);
					if (!load) {
						saveGame(filename);
						client.handleMenu(new InfoMenu("m8.info.ok",
								previousMenu));
					} else {
						loadGame(filename);
					}
				}
			});
		}

		if (!p_load) {
			items.add(new ItemMenu("m8.create") {
				@Override
				public void run() {
					// Save the game
					int i = 1;
					int temp;
					while (true) { // Find a filename which doesn't exist yet
						temp = i++;
						boolean found = false;
						for (String name : savegames) {
							int number = getSavegameNumber(name);
							if (!found && number == temp) {
								found = true;
							}
						}
						if (!found) {
							break;
						}
					}
					String filename = Constantes.SAVEGAME_DIR
							+ Constantes.SAVEGAME_FILE + temp;
					saveGame(filename);
					client.handleMenu(new InfoMenu("m8.info.ok", previousMenu));
				}
			});
		}

		items.add(new ItemMenu("global.back") {
			@Override
			public void run() {
				client.handleMenu(previousMenu);
			}
		});
		setMenu(items.toArray(new ItemMenu[]{}));
	}

	/**
	 * Save the game
	 */
	private void saveGame(String p_filename) {
		EasyBuffering buffer = new EasyBuffering();
		EngineZildo.game.serialize(buffer);
		EasyWritingFile file = new EasyWritingFile(buffer);
		file.saveFile(p_filename);
	}

	private void loadGame(String p_filename) {
		// Create a dummy game object, just to initialize server
		Game game = new Game(null, false);
		game.brandNew = false;
		SinglePlayer singlePlay = new SinglePlayer(game);

		EasyReadingFile file = new EasyReadingFile(p_filename);
		game = Game.deserialize(file);
		EngineZildo.setGame(game);
		EngineZildo.mapManagement.charge_map("d4"); //d4");

		singlePlay.launchGame();
	}

	/**
	 * Find all savegames in current directory.
	 * 
	 * @return List<String>
	 */
	public static List<String> findSavegame() {
		File saveDirectory = new File(Constantes.DATA_PATH
				+ Constantes.SAVEGAME_DIR);
		File[] savegames = saveDirectory.listFiles(new SaveGameFilter());
		List<String> filenames = new ArrayList<String>();
		if (savegames != null && savegames.length > 0) { // Is there any
															// savegames ?
			for (File f : savegames) {
				filenames.add(getSavegameDisplayTitle(f));
			}
		}

		return filenames;
	}

	/**
	 * Simple filter to get the game files.
	 */
	public static class SaveGameFilter implements FilenameFilter {

		public boolean accept(File dir, String name) {
			return name.startsWith(Constantes.SAVEGAME_FILE);
		}
	}

	/**
	 * Get the display for a savegame in File form.
	 * 
	 * @param p_file
	 * @return
	 */
	public static String getSavegameDisplayTitle(File p_file) {
		String name = p_file.getName().replace(Constantes.SAVEGAME_FILE, "");
		name += " "
				+ new SimpleDateFormat("dd.MM.yyyy HH-mm").format(new Date(
						p_file.lastModified()));
		return name;
	}

	public static int getSavegameNumber(String p_displayTitle) {
		return Integer.valueOf(p_displayTitle.split(" ")[0]);
	}

	public static String getSavegameFilename(int p_number) {
		return Constantes.SAVEGAME_DIR + Constantes.SAVEGAME_FILE + p_number;
	}
}
