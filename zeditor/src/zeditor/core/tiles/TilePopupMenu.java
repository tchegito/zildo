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

package zeditor.core.tiles;

import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import zeditor.windows.dialogs.CollisionDialog;
import zeditor.windows.managers.MasterFrameManager;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class TilePopupMenu extends JPopupMenu {

	JMenuItem item;
	Image image;
	
	public TilePopupMenu(final MasterFrameManager p_manager, final TileSelection p_tileSel, Image p_image) {
		item = new JMenuItem(new AbstractAction("Edit collision", null) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog frame = new CollisionDialog(p_manager, p_tileSel, image);
				frame.setLocationRelativeTo(item.getParent().getParent());
				frame.setModal(true);
				frame.setVisible(true);
			}
		});
		image = p_image;
        add(item);
	}
}
