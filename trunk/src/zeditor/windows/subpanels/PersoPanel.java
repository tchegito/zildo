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
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import zeditor.core.tiles.SpriteSet;
import zeditor.tools.ui.SizedGridPanel;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.fwk.ui.UIText;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.persos.PersoNJ;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.server.EngineZildo;

/**
 * @author Tchegito
 * 
 */
public class PersoPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4925564984668740468L;

	MasterFrameManager manager;

	JTextField name;
	JComboBox script;
	JComboBox angle;
	JComboBox persoType;
	JTextField object;
	JComboBox info;
	JSpinner spinner;
	JTextField dialogZone;
	JTextField dialogSwitch;

	Perso currentPerso;
	Behavior behavior;

	SizedGridPanel southPanel;
	PersoWidgetListener listener;
	boolean updatingUI; // To know wether user or UI ask for update

	public PersoPanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new SpriteSet(true, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		manager = p_manager;
	}

	@SuppressWarnings("serial")
	private JPanel getSouthPanel() {
		southPanel = new SizedGridPanel(8);

		name = new JTextField();
		southPanel.addComp(new JLabel("Nom"), name);
		script = new JComboBox(ZUtils.getValues(MouvementPerso.class));
		southPanel.addComp(new JLabel("Script"), script);

		persoType = new JComboBox(ZUtils.getValues(PersoDescription.class));
		southPanel.addComp(new JLabel("Type"), persoType);

		angle = new JComboBox(ZUtils.getValues(Angle.class));
		southPanel.addComp(new JLabel("Angle"), angle);

		object = new JTextField();
		southPanel.addComp(new JLabel("Objet"), object);

		info = new JComboBox(ZUtils.getValues(PersoInfo.class));
		southPanel.addComp(new JLabel("Info"), info);

		// Spinner for the dialogs
		spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(40, 12));
		initSpinner();
		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BorderLayout());
		subPanel.add(new JLabel("Dialog"), BorderLayout.WEST);
		subPanel.add(spinner, BorderLayout.CENTER);
		subPanel.add(new JButton(new AbstractAction("X", null) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (behavior != null) {
					int val = (Integer) spinner.getValue();
					behavior.replique[val] = 0;
					updateDialog();
				}
			}
		}), BorderLayout.EAST);

		// Display the corresponding sentenec
		dialogZone = new JTextField() {
			@Override
			public String getToolTipText() {
				return UIText.getGameText(this.getText());
			}
		};
		ToolTipManager.sharedInstance().registerComponent(dialogZone);

		southPanel.addComp(subPanel, dialogZone);

		dialogSwitch = new JTextField();
		southPanel.addComp(new JLabel("Switch"), dialogSwitch);

		// Now add the listeners
		listener = new PersoWidgetListener();
		name.getDocument().addDocumentListener(listener);
		persoType.addActionListener(listener);
		script.addActionListener(listener);
		angle.addActionListener(listener);
		object.addActionListener(listener);
		info.addActionListener(listener);
		spinner.addChangeListener(listener);
		dialogSwitch.getDocument().addDocumentListener(listener);
		dialogZone.getDocument().addDocumentListener(listener);

		return southPanel;
	}

	/**
	 * Update fields with the given character's infos.
	 * 
	 * @param p_entity
	 */
	public void focusPerso(Perso p_perso) {
		updatingUI = true; // To disable the listeners

		if (p_perso == null) {
			name.setText("");
			persoType.setSelectedIndex(0);
			script.setSelectedIndex(0);
			angle.setSelectedIndex(0);
			info.setSelectedIndex(0);
			object.setText("0");
			dialogSwitch.setText("");
			behavior = null;
		} else {
			name.setText(p_perso.getName());
			persoType.setSelectedIndex(p_perso.getDesc().ordinal());
			script.setSelectedIndex(p_perso.getQuel_deplacement().valeur);
			angle.setSelectedIndex(p_perso.getAngle().value);
			info.setSelectedIndex(p_perso.getInfo().ordinal());
			dialogSwitch.setText(p_perso.getDialogSwitch());
			object.setText("0");

			MapDialog mapDialog = EngineZildo.mapManagement.getCurrentMap()
					.getMapDialog();
			behavior = mapDialog.getBehaviors().get(p_perso.getName());
		}
		currentPerso = p_perso;
		updateDialog();

		// Initialize the spinner
		initSpinner();

		updatingUI = false;
	}

	private void initSpinner() {
		int max = 0;
		if (behavior != null) {
			for (int i = 0; i < behavior.replique.length; i++) {
				if (behavior.replique[i] == 0) {
					max = i;
				}
			}
		}
		spinner.setModel(new SpinnerNumberModel(0, 0, max, -1)); // -1 to get
																	// next
																	// sentence
																	// with down
																	// arrow
	}

	/**
	 * Update the dialog's textarea, according to the current Perso and the
	 * behavior (spinner's position)
	 */
	private void updateDialog() {
		MapDialog mapDialog = EngineZildo.mapManagement.getCurrentMap()
				.getMapDialog();
		int val = (Integer) spinner.getValue();
		String dial = mapDialog.getSentence(behavior, val);
		dialogZone.setText(dial);
		dialogZone.setCaretPosition(0);
	}

	/**
	 * Listener for all widgets in the "south panel" : name, angle, script, info
	 * and dialog.<br/>
	 * Manages the synchronisation between UI and model (Perso class)
	 * 
	 * @author Tchegito
	 * 
	 */
	class PersoWidgetListener implements ActionListener, DocumentListener,
			ChangeListener {

		@Override
		public void actionPerformed(ActionEvent actionevent) {
			if (!updatingUI) {
				Component comp = (Component) actionevent.getSource();
				if (comp == angle || comp == script || comp == info
						|| comp == persoType) {
					String val = (String) ((JComboBox) comp).getSelectedItem();
					if (comp == angle) {
						Angle a = ZUtils.getField(val, Angle.class);
						currentPerso.setAngle(a);
					} else if (comp == persoType) {
						PersoDescription p = ZUtils.getField(val,
								PersoDescription.class);
						currentPerso.setDesc(p);
					} else if (comp == script) {
						MouvementPerso s = ZUtils.getField(val,
								MouvementPerso.class);
						currentPerso.setQuel_deplacement(s);
					} else if (comp == info) {
						PersoInfo i = ZUtils.getField(val, PersoInfo.class);
						currentPerso.setInfo(i);
					}
				}
				manager.getZildoCanvas().setChangeSprites(true);
			}
		}

		@Override
		public void changedUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		@Override
		public void removeUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		@Override
		public void insertUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		@Override
		public void stateChanged(ChangeEvent changeevent) {
			updateDialog();
		}

		private void updateText(DocumentEvent p_event) {
			if (!updatingUI) {
				Component comp = KeyboardFocusManager
						.getCurrentKeyboardFocusManager().getFocusOwner();
				Document doc = p_event.getDocument();
				try {
					String txt = doc.getText(0, doc.getLength());
					if (comp == dialogZone) {
						updatePersoText(txt);
					} else if (comp == name) {
						currentPerso.setName(txt);
					} else if (comp == dialogSwitch) {
						if (!currentPerso.isZildo()) {
							PersoNJ pnj = (PersoNJ) currentPerso;
							pnj.setDialogSwitch(txt);
						}
					}
				} catch (BadLocationException e) {

				}
			}
		}

		private void updatePersoText(String p_text) {
			Area area = EngineZildo.mapManagement.getCurrentMap();
			MapDialog dialogs = area.getMapDialog();
			if (behavior == null) {
				behavior = new Behavior(name.getText());
				dialogs.addBehavior(behavior);
			}
			dialogs.setSentence(behavior, (Integer) spinner.getValue(), p_text);
		}
	}
}
