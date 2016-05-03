/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import zeditor.tools.ui.SizedGridPanel;
import zildo.client.ClientEngineZildo;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class ChangeOriginDialog extends JDialog {

	final JSpinner shiftX;
	final JSpinner shiftY;
	
	public ChangeOriginDialog() {
		setLayout(new BorderLayout());
		setTitle("Sprites collection");
	
		SizedGridPanel selPanel = new SizedGridPanel(2, 0);
		
		shiftX = new JSpinner(new SpinnerNumberModel(0, -64, 64, -1));
		shiftY = new JSpinner(new SpinnerNumberModel(0, -64, 64, -1));

		selPanel.addComp(new JLabel("Shift X"), shiftX);
		selPanel.addComp(new JLabel("Shift Y"), shiftY);
		
		add(selPanel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JButton(new AbstractAction("Change") {
			
			public void actionPerformed(ActionEvent e) {
				EngineZildo.mapManagement.getCurrentMap().shift((Integer) shiftX.getValue(), (Integer) shiftY.getValue());
				// Tell engine that any cached tile data should be cleared
				ClientEngineZildo.tileEngine.prepareTiles();
				// Close current window
				dispose();
			}
		}));
		add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
}
