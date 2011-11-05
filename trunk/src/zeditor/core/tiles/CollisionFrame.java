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

package zeditor.core.tiles;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import zildo.monde.map.TileInfo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class CollisionFrame extends JFrame {

	final static List<TileInfo> allPossibleTileInfo = new ArrayList<TileInfo>();
	
	static {
		for (int i=0;i<128;i++) {
			TileInfo t = TileInfo.fromInt(i);
			if (t != null) {
				allPossibleTileInfo.add(t);
			}
		}
		// Sort collision templates
		Collections.sort(allPossibleTileInfo, new Comparator<TileInfo>() {
			@Override
			public int compare(TileInfo o1, TileInfo o2) {
				return o1.template.compareTo(o2.template);
			}
		});
	}
	
	public CollisionFrame(Image img) {
		setLayout(new BorderLayout());
		setTitle("Collision templates");
		
		
		JPanel collPanel = new JPanel();
		// All possible collision templates
		collPanel.setLayout(new GridLayout(5, 10));
		Iterator<TileInfo> it = allPossibleTileInfo.iterator();
		for (int i=0;i<5 && it.hasNext();i++) {
			for (int j=0;j<10 && it.hasNext();j++) {
				JPanel panel = new JPanel();
				// Duplicate image
				Image duplicata = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
				Graphics g = duplicata.getGraphics();
				g.drawImage(img, 0, 0, 16, 16, 
						0, 0, 16, 16, null); 
				CollisionDrawer collisionDrawer = new CollisionDrawer(g);
				collisionDrawer.drawCollisionTile(0, 0, it.next());
				panel.add(new JLabel(new ImageIcon(duplicata)));
				collPanel.add(panel);
			}
		}
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(new AbstractAction("OK", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}));
		
		add(new JLabel("Select a template for this tile :"), BorderLayout.NORTH);
		add(collPanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
		pack();
	}
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				CollisionFrame inst = new CollisionFrame(null);
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
}
