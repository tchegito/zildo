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

package zeditor.windows.subpanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import zeditor.core.selection.SpriteSelection;
import zeditor.core.tiles.SpriteSet;
import zeditor.tools.ui.SizedGridPanel;
import zeditor.windows.managers.MasterFrameManager;
import zildo.monde.sprites.Rotation;
import zildo.monde.sprites.SpriteEntity;
import zildo.monde.sprites.desc.EntityType;
import zildo.monde.sprites.desc.SpriteDescription;
import zildo.monde.sprites.desc.ZSpriteLibrary;
import zildo.monde.sprites.elements.Element;

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

	JCheckBox reverseHorizontal;
	JCheckBox reverseVertical;
	JCheckBox foreground;
	JComboBox spriteType;
	JComboBox rotation;
	JCheckBox pushable;
	JSpinner spinX;
	JSpinner spinY;
	JSpinner repeatX;
	JSpinner repeatY;
	JLabel entityType;
	JLabel floor;
	JTextField elementName;
	JScrollPane scrollPane;
	
	boolean updatingUI;

	SpriteSelection<SpriteEntity> sel;

	SpriteSet spriteSet;
	
	SpriteEntity entity;
	List<SpriteEntity> entities; // We can modify a global list of entities

	final List<SpriteDescription> spriteLib = ZSpriteLibrary.getList();

	public SpritePanel(MasterFrameManager p_manager) {
		spriteSet = new SpriteSet(false, p_manager);
		
		setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setViewportView(spriteSet);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBar(new TweekedScrollbar());
		add(scrollPane, BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		manager = p_manager;
	}

	@SuppressWarnings("serial")
	private JPanel getSouthPanel() {
		SizedGridPanel panel = new SizedGridPanel(9, 0);

		foreground = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					sel.toggleForeground();
					manager.setUnsavedChanges(true);
				}
			}
		});

		entityType = new JLabel();
		JPanel panelKindName = new JPanel();
		panelKindName.add(entityType);
		elementName = new JTextField(12);
		panelKindName.add(elementName);
		Dimension d = elementName.getPreferredSize();
		d.height = entityType.getPreferredSize().height;
		panelKindName.setPreferredSize(d);
		panel.addComp(new JLabel("Kind - name"), panelKindName);

		spriteType = new JComboBox(spriteLib.toArray());
		panel.addComp(new JLabel("Type"), spriteType);

		JPanel panelForegroundPushable = new JPanel();
		pushable = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					togglePushable(true);
				}
			}
		});
		panelForegroundPushable.add(foreground);
		panelForegroundPushable.add(new JLabel("Foreground")); 
		panelForegroundPushable.add(pushable);
		panelForegroundPushable.add(new JLabel("Pushable")); 
		panel.addComp(floor = new JLabel("Floor: "), panelForegroundPushable);
		
		spinX = new JSpinner(new SpinnerNumberModel(0, -1, 16, -1));
		spinY = new JSpinner(new SpinnerNumberModel(0, -1, 16, -1));

		panel.addComp(new JLabel("Add-x"), spinX);

		panel.addComp(new JLabel("Add-y"), spinY);

		// Reverse
		reverseHorizontal = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					toggleReverse(true);
				}
			}

		});
		reverseVertical = new JCheckBox(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if (sel != null) {
					toggleReverse(false);
				}
			}

		});
		JPanel panelReverse = new JPanel();
		panelReverse.add(reverseHorizontal);
		panelReverse.add(new JLabel("H"));
		panelReverse.add(reverseVertical);
		panelReverse.add(new JLabel("V"));
		panel.addComp(new JLabel("Reverse"), panelReverse);

		// Rotation
		rotation = new JComboBox(Rotation.values());
		panel.addComp(new JLabel("Rotation"), rotation);
		
		SpriteFieldsListener listener = new SpriteFieldsListener();
		spriteType.addActionListener(listener);
		rotation.addActionListener(listener);
		spinX.addChangeListener(listener);
		spinY.addChangeListener(listener);
		elementName.getDocument().addDocumentListener(listener);
		
		// Repeat fields
		repeatX = new JSpinner(new SpinnerNumberModel(1, 1, 127, -1));
		repeatY = new JSpinner(new SpinnerNumberModel(1, 1, 127, -1));
		repeatX.addChangeListener(listener);
		repeatY.addChangeListener(listener);

		panel.addComp(new JLabel("Repeat-x"), repeatX);

		panel.addComp(new JLabel("Repeat-y"), repeatY);
		return panel;
	}

	private void toggleReverse(boolean p_horizontal) {
		sel.reverse(p_horizontal);
		// Ask a sprite visual update
		manager.getZildoCanvas().setChangeSprites(true);
		manager.setUnsavedChanges(true);
	}

	private void togglePushable(boolean p_pushable) {
		for (SpriteEntity ent : sel.getElement()) {
			if (ent.getEntityType().isElement()) {
				Element elem = (Element) ent;
				elem.setPushable(!elem.isPushable());
				manager.setUnsavedChanges(true);
			}
		}
	}

	/**
	 * Update fields with the given entity's infos.
	 * 
	 * @param p_entity
	 */
	private void focusSprite(SpriteEntity p_entity) {
		updatingUI = true;
		if (p_entity == null) {
			// Reset fields
			entityType.setText("");
			spinX.setValue(0);
			spinY.setValue(0);
			spriteType.setSelectedIndex(0);
			reverseHorizontal.setSelected(false);
			reverseVertical.setSelected(false);
			rotation.setSelectedIndex(0);
			foreground.setSelected(false);
			pushable.setSelected(false);
			repeatX.setValue(1);
			repeatY.setValue(1);
			elementName.setText("");
			floor.setText("Floor:");
		} else {
			EntityType kind = p_entity.getEntityType();
			entityType.setText(kind.toString());
			spriteType.setSelectedIndex(spriteLib.indexOf(p_entity.getDesc()));
			spinX.setValue((int) p_entity.x % 16);
			spinY.setValue((int) p_entity.y % 16);
			reverseHorizontal.setSelected(p_entity.reverse.isHorizontal());
			reverseVertical.setSelected(p_entity.reverse.isVertical());
			rotation.setSelectedIndex(p_entity.rotation.value);
			foreground.setSelected(p_entity.isForeground());
			repeatX.setValue((int) p_entity.repeatX);
			repeatY.setValue((int) p_entity.repeatY);
			String name = "";
			name = p_entity.getName();
			pushable.setSelected(false);
			if (kind.isElement()) {
				Element elem = (Element) p_entity;
				pushable.setSelected(elem.isPushable());
			}
			elementName.setText(name);
			floor.setText("Floor:"+p_entity.getFloor());
		}
		updatingUI = false;
		entity = p_entity;
	}

	public void focusSprites(SpriteSelection<SpriteEntity> p_sel) {
		entities = null;
		sel = p_sel;
		if (p_sel == null) {
			focusSprite(null);
		} else {
			List<SpriteEntity> ent = p_sel.getElement();
			if (ent.size() == 1) {
				focusSprite(ent.get(0));
			} else {
				// We're dealing with a list
				entities = ent;
				focusSprite(null);
			}
		}
	}

	class SpriteFieldsListener implements ChangeListener, ActionListener, DocumentListener {

		@Override
		public void stateChanged(ChangeEvent changeevent) {

			// If we are focusing on an existing sprite, then update his
			// attributes
			if (!updatingUI && sel != null) {
				Component comp = (Component) changeevent.getSource();
				if (comp instanceof JSpinner) {
					JSpinner spinner = (JSpinner) comp;
					int val = (Integer) spinner.getValue();
					if (comp == spinX) {
						sel.addX(val);
					} else if (comp == spinY) {
						sel.addY(val);
					} else if (comp == repeatX){
						sel.setRepeatX(val);
					} else if (comp == repeatY) {
						sel.setRepeatY(val);
					}
					// Wrap values (only for addX, addY)
					if (comp == spinX || comp == spinY) {
						if (val == 16) {
							spinner.setValue(0);
						} else if (val == -1) {
							spinner.setValue(15);
						}
					}
					sel.setFloor(manager.getCurrentFloor());
					focusSprites(sel);
					manager.getZildoCanvas().setChangeSprites(true);
					manager.setUnsavedChanges(true);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent actionevent) {
			if (!updatingUI && sel != null) {
				Component comp = (Component) actionevent.getSource();
				if (comp == spriteType) {
					SpriteDescription desc = (SpriteDescription) spriteType.getSelectedItem();
					entity.setDesc(desc);
				} else if (comp == rotation) {
					Rotation rot = (Rotation) rotation.getSelectedItem();
					entity.rotation = rot;
				}
				manager.getZildoCanvas().setChangeSprites(true);
				manager.setUnsavedChanges(true);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateText(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateText(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateText(e);
		}
	}

	
	private void updateText(DocumentEvent e) {
		if (!updatingUI && entity != null) {
			Document doc = e.getDocument();
			try {
				String txt = doc.getText(0, doc.getLength());
				entity.setName(txt);
				manager.setUnsavedChanges(true);
			} catch (Exception ex) {
				
			}
		}
	}
	
	public SpriteSet getSpriteSet() {
		return spriteSet;
	}
}
