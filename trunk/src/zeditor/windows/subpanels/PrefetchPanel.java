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

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
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
 * @author Tchegito
 *
 */
public class PrefetchPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6076376476223285815L;
	JList prefetchList;
	MasterFrameManager manager;
	
	public PrefetchPanel(MasterFrameManager p_manager) {
		manager=p_manager;
		
		BoxLayout prefetchPanelLayout = new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS);
		setLayout(prefetchPanelLayout);
		add(getCombo());
	}
			
	public JList getCombo() {
		if (prefetchList == null) {
			ComboBoxModel prefetchListModel = new DefaultComboBoxModel(manager.getPrefetchForCombo());
			prefetchList = new JList();
			prefetchList.setModel(prefetchListModel);
			prefetchList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			prefetchList.setLayoutOrientation(JList.VERTICAL);
			prefetchList.setVisibleRowCount(-1);
			prefetchList.setSize(500,300);
			prefetchList.addListSelectionListener(new ListSelectionListener() {
				
				public void valueChanged(ListSelectionEvent e) {
				    if (e.getValueIsAdjusting() == false) {
	
						int ind=prefetchList.getSelectedIndex();
				        if (ind != -1) {
							manager.setCaseSelection(new PrefetchSelection(Prefetch.fromInt(ind)));
				        }
				    }
				}
			});
		}
		return prefetchList;
	}
}
