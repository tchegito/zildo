/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zeditor.core.tiles.SpriteSet;
import zeditor.tools.ui.SizedGridPanel;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 *
 */
@SuppressWarnings("serial")
public class ViewSpritesDialog extends JDialog {

	JComboBox<String> comboBank;
	int[] numSpritesInBank;
	JLabel spriteImgLabel;
	JSpinner spriteSpinner;
	
	
	public ViewSpritesDialog() {
		setLayout(new BorderLayout());
		setTitle("Sprites collection");
	
		SizedGridPanel selPanel = new SizedGridPanel(2, 0);
		
		String[] bankNames = SpriteStore.sprBankName;
		numSpritesInBank = new int[bankNames.length];
		for (int i=0;i<bankNames.length;i++) {
			SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(i);
			numSpritesInBank[i] = bank.getModels().size();
			bankNames[i] = i+" - " + bankNames[i] + " ("+numSpritesInBank[i]+" sprites)";
		}
		comboBank = new JComboBox<String>(bankNames);
		
		ViewSpritesListener listener = new ViewSpritesListener();
		comboBank.addActionListener(listener);
		selPanel.addComp(new JLabel("Bank:"), comboBank);
		spriteSpinner = new JSpinner();
		spriteSpinner.addChangeListener(listener);
		selPanel.addComp(new JLabel("Number:"), spriteSpinner);
		add(selPanel, BorderLayout.NORTH);
		
		JPanel visuPanel = new JPanel();
		visuPanel.setPreferredSize(new Dimension(200, 200));
		spriteImgLabel = new JLabel();
		visuPanel.add(spriteImgLabel);

		updateSpinner();
		display();

		add(visuPanel, BorderLayout.SOUTH);
		
		pack();
	}
	
	private void updateSpinner() {
		int nBank = comboBank.getSelectedIndex();
		spriteSpinner.setModel(new SpinnerNumberModel(0, 0, numSpritesInBank[nBank]-1, -1));
	}
	
	private void display() {
		int nBank = comboBank.getSelectedIndex();
		int nSpr = (Integer) spriteSpinner.getValue();
		SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(nBank);
		SpriteModel model = bank.getModels().get(nSpr);
		Image img = new BufferedImage(model.getTaille_x(), model.getTaille_y(), BufferedImage.TYPE_INT_RGB);
		Graphics2D gfx2d = (Graphics2D) img.getGraphics();
		SpriteSet.drawSprite(0, 0, bank, nSpr, gfx2d);
		ImageIcon icon = new ImageIcon(img);
		spriteImgLabel.setIcon(icon);
	}
	
	class ViewSpritesListener implements ActionListener, ChangeListener {
		@Override
		public void actionPerformed(ActionEvent actionevent) {
			// Sprite Bank has changed
			updateSpinner();
			display();
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			display();
		}
	}
}