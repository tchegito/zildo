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

package zeditor.windows.subpanels;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import zeditor.core.prefetch.Prefetch;
import zeditor.core.prefetch.PrefetchSelection;
import zeditor.windows.managers.MasterFrameManager;

/**
 * Panel into the tabbed panes : Chaining points.
 * 
 * @author Tchegito
 *
 */
public class ChainingPointPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7870707104640951490L;
	JList pointsList;
	
	MasterFrameManager manager;
	
	public ChainingPointPanel(MasterFrameManager p_manager) {
		BorderLayout chainingPointPanelLayout = new BorderLayout();
		setLayout(chainingPointPanelLayout);
		add(getCombo(), BorderLayout.WEST);
		
		manager=p_manager;
	}
	
	public JList getCombo() {
		if (pointsList == null) {
			pointsList = new JList();
			pointsList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			pointsList.setLayoutOrientation(JList.VERTICAL);
			pointsList.setVisibleRowCount(-1);
			pointsList.setSize(500,300);
			pointsList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
				    if (e.getValueIsAdjusting() == false) {
	
						int ind=pointsList.getSelectedIndex();
				        if (ind != -1) {
							//manager.setChainingPointSelection(this);
				        }
				    }
				}
			});
		}
		return pointsList;
	}
	
	public void updateList(Object[] p_points) {
		pointsList.setModel(new DefaultComboBoxModel(p_points));
	}
}
