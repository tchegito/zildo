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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
	JCheckBox reverseHorizontal;
	JCheckBox reverseVertical;
	
	SpriteEntity entity;
	
	public SpritePanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new SpriteSet(false, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);
		
		manager=p_manager;
	}

	@SuppressWarnings("serial")
	private JPanel getSouthPanel() {
		JPanel panel=new JPanel();
		GridLayout thisLayout = new GridLayout(0,2);
		panel.setLayout(thisLayout);
		
		addx=new JFormattedTextField(0);
		panel.add(new JLabel("Add-x"));
		panel.add(addx);
		
		addy=new JFormattedTextField(0);
		panel.add(new JLabel("Add-y"));
		panel.add(addy);
		
		reverseHorizontal=new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (entity != null) {
					toggleReverse(true);
				}
			}
			
		});
		panel.add(new JLabel("Reverse H"));
		panel.add(reverseHorizontal);

		reverseVertical=new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (entity != null) {
					toggleReverse(false);
				}
			}
			
		});
		panel.add(new JLabel("Reverse V"));
		panel.add(reverseVertical);

		DocumentListener listener=new SpriteFieldsListener();
		addx.getDocument().addDocumentListener(listener);
		addy.getDocument().addDocumentListener(listener);
		
		return panel;
	}
	
	private void toggleReverse(boolean p_horizontal) {
		boolean isHorizontal=(entity.reverse & SpriteEntity.REVERSE_HORIZONTAL) != 0;
		boolean isVertical=(entity.reverse & SpriteEntity.REVERSE_VERTICAL) != 0;
		
		if (p_horizontal) {
			isHorizontal=!isHorizontal;
		} else {
			isVertical=!isVertical;
		}
		
		entity.reverse=isVertical ? SpriteEntity.REVERSE_VERTICAL : 0;
		entity.reverse|=isHorizontal ? SpriteEntity.REVERSE_HORIZONTAL : 0;
		// Ask a sprite visual update
		manager.getZildoCanvas().setChangeSprites(true);
	}
	
	/**
	 * Update fields with the given entity's infos.
	 * @param p_entity
	 */
	public void focusSprite(SpriteEntity p_entity) {
		if (p_entity == null) {
			// Reset fields
			addx.setText("0");
			addy.setText("0");
			reverseHorizontal.setSelected(false);
			reverseVertical.setSelected(false);
		} else {
			addx.setText(String.valueOf(p_entity.x % 15));
			addy.setText(String.valueOf(p_entity.y % 15));
			reverseHorizontal.setSelected(0 != (p_entity.reverse & SpriteEntity.REVERSE_HORIZONTAL));
			reverseVertical.setSelected(0 != (p_entity.reverse & SpriteEntity.REVERSE_VERTICAL));
		}
		entity=p_entity;
	}
	
	class SpriteFieldsListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		@Override
		public void insertUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		@Override
		public void removeUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}
		
		private void updateText(DocumentEvent documentevent) {
			// If we are focusing on an existing sprite, then update his attributes
			if (entity != null) {
				Component comp=KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				if (comp instanceof JTextField) {
					JTextField textField=(JTextField) comp;
					String value=textField.getText();
					int val=0;
					try {
						val=Integer.valueOf(value);
					} catch (NumberFormatException e) {
					}
					val = val % 16;
					if (textField == addx) {
						entity.x=16*(int) (entity.x / 16) + val;
						entity.setAjustedX((int) entity.x);
						//textField.setText(String.valueOf(val));
					} else if (textField == addy) {
						//textField.setText(String.valueOf(val));
						entity.y=16*(int) (entity.y / 16) + val;
						entity.setAjustedY((int) entity.y);
					}
					manager.getZildoCanvas().setChangeSprites(true);
				}
		    	
			}			
		}
	}
}
