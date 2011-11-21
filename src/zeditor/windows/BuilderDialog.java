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

package zeditor.windows;

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

import zeditor.tools.sprites.Modifier;
import zeditor.tools.ui.SizedGridPanel;
import zildo.fwk.gfx.engine.TileEngine;

@SuppressWarnings("serial")
public class BuilderDialog extends JDialog {

	JComboBox comboBank;
	
	public BuilderDialog() {
		SizedGridPanel panel = new SizedGridPanel(2);
		
		// Tile bank combo
		List<String> list = new ArrayList<String>();
		for (String bankName : TileEngine.tileBankNames) {
			list.add(bankName);
		}
		comboBank = new JComboBox(list.toArray(new String[]{}));
		JButton buttonBuildBank = new JButton(new AbstractAction("Build bank") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String bankName = (String) comboBank.getSelectedItem();

				new Modifier().saveNamedTileBank(bankName);
				JOptionPane.showMessageDialog(getParent(), "Bank "+bankName+" has been builded !", "Builder", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		panel.addComp(comboBank, buttonBuildBank);
		
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
	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BuilderDialog inst = new BuilderDialog();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
}
