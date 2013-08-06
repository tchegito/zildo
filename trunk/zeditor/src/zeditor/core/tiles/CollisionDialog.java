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

package zeditor.core.tiles;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.map.Tile;
import zildo.monde.map.TileCollision;
import zildo.monde.map.TileInfo;

/**
 * @author Tchegito
 * 
 */
@SuppressWarnings("serial")
public class CollisionDialog extends JDialog {

	final MasterFrameManager manager;

	// Py every collision template at a user-friendly' location
	final int[][] disposition = {
			{ -1, -1, -1, -1, 6, -1,- 1, -1, -1},
			{ -1, 90, 115, 117, 4, 69, 67, 106, -1 },
			{ -1, 98, 116, 91, -1, 107, 68, 82, -1 },
			{ 54, 52, 125, 109, 1, 77, 93, 20, 22 },
			{ -1, 114, 100, 75, 0, 123, 84, 66, -1 },
			{ -1, 74, 99, 101, 36, 85, 83, 122, -1 },
			{ -1, -1, -1, -1, 38, -1,- 1, -1, -1}};

	public CollisionDialog(MasterFrameManager p_manager, Tile p_tile, Image p_img) {
		setLayout(new BorderLayout());
		setTitle("Collision templates");

		manager = p_manager;

		JPanel collPanel = new JPanel();
		// All possible collision templates
		collPanel.setLayout(new GridLayout(7, 12));

		for (int[] element : disposition) {
			for (int j = 0; j < 9; j++) {
				int hashTile = element[j];
				JPanel panel = new JPanel();
				if (hashTile != -1) {
					// Duplicate image
					Image duplicata = new BufferedImage(32, 32,
							BufferedImage.TYPE_INT_RGB);
					Graphics g = duplicata.getGraphics();
					g.drawImage(p_img, 0, 0, 32, 32,
							0, 0, 16, 16, null);
					CollisionDrawer collisionDrawer = new CollisionDrawer(g);
					TileInfo info = TileInfo.fromInt(hashTile);
					collisionDrawer.drawCollisionTile(0, 0, info, true);
					panel.add(new CollisionTemplateButton(new ImageIcon(duplicata), p_tile, info));
				}
				collPanel.add(panel);
			}
		}
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(new AbstractAction("Cancel", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}));

		add(new JLabel("Select a template for this tile :"), BorderLayout.NORTH);
		add(collPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
		pack();

		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				manager.updateTileSet();
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
	}

	private class CollisionTemplateButton extends JButton {

		public CollisionTemplateButton(ImageIcon p_img, final Tile p_tile, final TileInfo p_info) {
			setAction(new AbstractAction("", p_img) {
				@Override
				public void actionPerformed(ActionEvent e) {
					TileCollision.getInstance().updateInfoForOneTile(p_tile.bank, p_tile.index, p_info);
					dispose();
				}
			});
		}
	}

}
