/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
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

package zeditor.tools.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 * Combobox with adjustable popup.<br/>
 * Code found at "http://www.codeguru.com/java/articles/163.shtml".
 * 
 * @author Tchegito
 * 
 */
@SuppressWarnings("serial")
public class SteppedComboBox extends JComboBox {
	protected int popupWidth;

	public SteppedComboBox(ComboBoxModel aModel) {
		super(aModel);
		setUI(new SteppedComboBoxUI());
		popupWidth = 0;
	}

	public SteppedComboBox(final Object[] items) {
		super(items);
		setUI(new SteppedComboBoxUI());
		popupWidth = 0;
	}

	public SteppedComboBox(Vector<?> items) {
		super(items);
		setUI(new SteppedComboBoxUI());
		popupWidth = 0;
	}

	public void setPopupWidth(int width) {
		popupWidth = width;
	}

	public Dimension getPopupSize() {
		Dimension size = getSize();
		if (popupWidth < 1) {
			popupWidth = size.width;
		}
		return new Dimension(popupWidth, size.height);
	}
}

class SteppedComboBoxUI extends MetalComboBoxUI {
	@Override
	protected ComboPopup createPopup() {
		@SuppressWarnings("serial")
		BasicComboPopup bcPopup = new BasicComboPopup(comboBox) {

			@Override
			public void show() {
				Dimension popupSize = ((SteppedComboBox) comboBox)
						.getPopupSize();
				popupSize
						.setSize(popupSize.width,
								getPopupHeightForRowCount(comboBox
										.getMaximumRowCount()));
				Rectangle popupBounds = computePopupBounds(0,
						comboBox.getBounds().height, popupSize.width,
						popupSize.height);
				scroller.setMaximumSize(popupBounds.getSize());
				scroller.setPreferredSize(popupBounds.getSize());
				scroller.setMinimumSize(popupBounds.getSize());
				list.invalidate();
				int selectedIndex = comboBox.getSelectedIndex();
				if (selectedIndex == -1) {
					list.clearSelection();
				} else {
					list.setSelectedIndex(selectedIndex);
				}
				list.ensureIndexIsVisible(list.getSelectedIndex());
				setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

				show(comboBox, popupBounds.x, popupBounds.y);
			}
		};
		bcPopup.getAccessibleContext().setAccessibleParent(comboBox);
		return bcPopup;
	}
}
