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

package zeditor.windows.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import zeditor.tools.builder.Modifier;
import zeditor.tools.ui.SizedGridPanel;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.engine.TileEngine;
import zildo.monde.sprites.SpriteStore;

@SuppressWarnings("serial")
public class BuilderDialog extends JDialog {

	JComboBox comboTileBank;
	JComboBox comboSpriteBank;
	
	final MasterFrameManager manager;
	
	public BuilderDialog(MasterFrameManager pManager) {
		manager = pManager;
		SizedGridPanel panel = new SizedGridPanel(4, 5);

		// Tile bank combo
		List<String> listTileBanks = new ArrayList<String>();
		for (String bankName : TileEngine.tileBankNames) {
			listTileBanks.add(bankName);
		}
		comboTileBank = new JComboBox(listTileBanks.toArray(new String[]{}));
		JButton buttonBuildTileBank = new JButton(new AbstractAction("Build tile bank") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String bankName = (String) comboTileBank.getSelectedItem();

				new Modifier().saveNamedTileBank(bankName);
				
				// Reload all banks
				reloadTiles();

				info("Tile bank "+bankName+" has been builded !");
			}
		});
		panel.addComp(comboTileBank, buttonBuildTileBank);
				
		// All motif bank
		JButton buttonAllTileBanks = new JButton(new AbstractAction("Build all tile banks") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new Modifier().saveAllMotifBank();

					// Reload all banks
					reloadTiles();

					info("All tile banks builded successfully !");
				} catch (RuntimeException ex) {
					error("Error building all tile banks !", ex);
				}
			}
		});
		panel.add(buttonAllTileBanks);
		
		// Sprite bank combo
		List<String> listSpriteBanks = new ArrayList<String>();
		for (String bankName : SpriteStore.sprBankName) {
			listSpriteBanks.add(bankName);
		}
		comboSpriteBank = new JComboBox(listSpriteBanks.toArray(new String[]{}));
		JButton buttonBuildSpriteBank = new JButton(new AbstractAction("Build sprite bank") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String bankName = (String) comboSpriteBank.getSelectedItem();

				try {
					new Modifier().saveNamedSpriteBank(bankName);
					info("Sprite bank "+bankName+" has been builded !");
				} catch (RuntimeException ex) {
					error("Error building bank "+bankName, ex);
				}
			}
		});
		panel.addComp(comboSpriteBank, buttonBuildSpriteBank);

		panel.add(new JButton(new AbstractAction("Build textures") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Save all textures
					manager.getZildoCanvas().askBuildTexture();

					info("Texture have been builded !");
				} catch (Exception ex) {
					error("Error building textures !\n\nCause : ", ex);
				}
			}
		}));
	
		setTitle("ZEditor builder");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(new JLabel("Build your own thing."), BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(new JButton(new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}), BorderLayout.SOUTH);
		
		pack();
	}
	
	private void reloadTiles() {
		// Reload DEC files
		ClientEngineZildo.tileEngine.loadAllTileBanks();
		// Ask image in background panel to rebuild, even current one
		manager.reloadTileBanks();
		// Ask graphic card to reload its current textures
		manager.getZildoCanvas().askReloadTexture();
	}
	
	private void info(String message) {
		JOptionPane.showMessageDialog(getParent(), message, "Builder", JOptionPane.INFORMATION_MESSAGE);		
	}
	
	private void error(String message, Exception ex) {
		JOptionPane.showMessageDialog(getParent(), message + ex.getMessage(), "Builder", JOptionPane.ERROR_MESSAGE);
		ex.printStackTrace();
	}
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BuilderDialog inst = new BuilderDialog(null);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
}
