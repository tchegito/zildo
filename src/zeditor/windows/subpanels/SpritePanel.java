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

package zeditor.windows.subpanels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import zeditor.core.tiles.SpriteSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.sprites.SpriteEntity;

/**
 * @author Tchegito
 *
 */
public class SpritePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -591070226658678002L;

	MasterFrameManager manager;
	
	JTextField addx;
	JTextField addy;
	
	public SpritePanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new SpriteSet(false, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);
		
		manager=p_manager;
	}

	private JPanel getSouthPanel() {
		JPanel panel=new JPanel();
		GridLayout thisLayout = new GridLayout(0,2);
		panel.setLayout(thisLayout);
		
		addx=new JTextField();
		panel.add(new JLabel("Add-x"));
		panel.add(addx);
		
		addy=new JTextField();
		panel.add(new JLabel("Add-y"));
		panel.add(addy);
		
		return panel;
	}
	/**
	 * Update fields with the given entity's infos.
	 * @param p_entity
	 */
	public void focusSprite(SpriteEntity p_entity) {
		addx.setText(String.valueOf(p_entity.x % 15));
		addy.setText(String.valueOf(p_entity.y % 15));
	}
}
