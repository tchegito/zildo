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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.bank.SpriteBank;
import zildo.monde.sprites.SpriteModel;
import zildo.monde.sprites.SpriteStore;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.util.Zone;
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
	JLabel sizeLabel;
	JSpinner spriteSpinner;
	
	MasterFrameManager manager;
	SpriteSet spriteSet;
	
	public ViewSpritesDialog(MasterFrameManager manager, SpriteSet spriteSet) {
		this.manager = manager;
		this.spriteSet = spriteSet;
		
		setLayout(new BorderLayout());
		setTitle("Sprites collection");
	
		SizedGridPanel selPanel = new SizedGridPanel(2, 0);
		
		String[] bankNames = SpriteStore.sprBankName;
		String[] comboNames = new String[bankNames.length];
		numSpritesInBank = new int[bankNames.length];
		for (int i=0;i<bankNames.length;i++) {
			SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(i);
			numSpritesInBank[i] = bank.getModels().size();
			comboNames[i] = i+" - " + bankNames[i] + " ("+numSpritesInBank[i]+" sprites)";
		}
		comboBank = new JComboBox<String>(comboNames);
		
		ViewSpritesListener listener = new ViewSpritesListener();
		comboBank.addActionListener(listener);
		selPanel.addComp(new JLabel("Bank:"), comboBank);
		spriteSpinner = new JSpinner();
		spriteSpinner.addChangeListener(listener);
		selPanel.addComp(new JLabel("Number:"), spriteSpinner);
		add(selPanel, BorderLayout.NORTH);
		
		sizeLabel = new JLabel();
		
		JPanel visuPanel = new JPanel(new BorderLayout());
		visuPanel.setPreferredSize(new Dimension(300, 200));
		visuPanel.add(sizeLabel, BorderLayout.NORTH);
		spriteImgLabel = new JLabel();
		spriteImgLabel.setHorizontalAlignment(JLabel.CENTER);
		visuPanel.add(spriteImgLabel, BorderLayout.CENTER);
		BuilderDialog bd = new BuilderDialog(manager);
		add(new JButton(bd.buildSpriteBankAction(comboBank)), BorderLayout.SOUTH);
		
		updateSpinner();
		display();

		add(visuPanel, BorderLayout.CENTER);
		
		pack();
	}
	
	private void updateSpinner() {
		int nBank = comboBank.getSelectedIndex();
		spriteSpinner.setModel(new SpinnerNumberModel(0, 0, numSpritesInBank[nBank]-1, -1));
	}
	
	private void display() {
		int scale = 4;
		int nBank = comboBank.getSelectedIndex();
		int nSpr = (Integer) spriteSpinner.getValue();
		SpriteBank bank = EngineZildo.spriteManagement.getSpriteBank(nBank);
		SpriteModel model = bank.getModels().get(nSpr);
		

		int tx = model.getTaille_x();
		Zone z = model.getEmptyBorders();
		if (z != null) {
			tx += z.x1 + z.x2;
		}
		ByteBuffer buffer = manager.getSpriteTexture(nBank);
		Image img = new BufferedImage(tx*scale, model.getTaille_y()*scale, BufferedImage.TYPE_INT_RGB);
		Graphics2D gfx2d = (Graphics2D) img.getGraphics();
		// Multiply size by scale
		gfx2d.scale(scale, scale);
		// Display width/height
		String display = model.getTaille_x()+" x "+model.getTaille_y();
		Zone borders = model.getEmptyBorders();
		int startX = 0, sizeX = model.getTaille_x()*scale;
		if (borders != null) {
			display += " offXLeft="+borders.x1+" offXRight="+borders.x2+" offY="+borders.y1;
		}
		gfx2d.fillRect(startX, 0, sizeX, model.getTaille_y()*scale);
		gfx2d.translate(0.5, 0.5);
		spriteSet.drawSprite(0, 0, bank, nSpr, gfx2d, buffer);
		ImageIcon icon = new ImageIcon(img);
		spriteImgLabel.setIcon(icon);

		display += " " +SpriteDescription.Locator.findSpr(nBank, nSpr);
		sizeLabel.setText(display);
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