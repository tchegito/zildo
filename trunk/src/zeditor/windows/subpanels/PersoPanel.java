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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import zeditor.core.tiles.SpriteSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Angle;
import zildo.monde.map.Area;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
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
	JTextField object;
	JComboBox info;
	JSpinner spinner;
	JTextArea dialogZone;
	
	Perso currentPerso;
	Behavior behavior;
	
	JPanel southPanel;
	PersoWidgetListener listener;
	boolean updatingUI;	// To know wether user or UI ask for update
	
	public PersoPanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new SpriteSet(true, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		manager=p_manager;
	}
	
	private JPanel getSouthPanel() {
		southPanel=new JPanel();
		southPanel.setLayout(new GridLayout(6, 1));

		name=new JTextField();
		addComp(new JLabel("Nom"), name);
		script=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(MouvementPerso.class)));
		addComp(new JLabel("Script"), script);
		
		angle=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(Angle.class)));
		addComp(new JLabel("Angle"), angle);

		object=new JTextField();
		addComp(new JLabel("Objet"), object);
		
		info=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(PersoInfo.class)));
		addComp(new JLabel("Info"), info);
		
		// Spinner for the dialogs
		spinner = new JSpinner();
		spinner.setPreferredSize(new Dimension(40,12));
		initSpinner();
		JPanel subPanel=new JPanel();
		subPanel.setLayout(new BorderLayout());
		subPanel.add(new JLabel("Dialog"), BorderLayout.WEST);
		subPanel.add(spinner, BorderLayout.EAST);
		
		dialogZone=new JTextArea(3, 30);
		dialogZone.setLineWrap(true);
		dialogZone.setWrapStyleWord(true);
		JScrollPane areaScrollPane = new JScrollPane(dialogZone);
	        areaScrollPane.setVerticalScrollBarPolicy(
	                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		addComp(subPanel, areaScrollPane);

		// Now add the listeners
		PersoWidgetListener listener=new PersoWidgetListener();
		name.getDocument().addDocumentListener(listener);
		script.addActionListener(listener);
		angle.addActionListener(listener);
		object.addActionListener(listener);
		info.addActionListener(listener);
		spinner.addChangeListener(listener);
		dialogZone.getDocument().addDocumentListener(listener);
		
		return southPanel;
	}
	
	private void addComp(Component p_compLeft, Component p_compRight) {
	    Dimension d=p_compRight.getPreferredSize();

	    JPanel currentPanel=new JPanel();
		BorderLayout layout=new BorderLayout();
		layout.setHgap(10);
		currentPanel.setLayout(layout);
		p_compRight.setPreferredSize(new Dimension(2*SpriteSet.width/3, d.height));

	    currentPanel.add(p_compLeft, BorderLayout.WEST);
	    currentPanel.add(p_compRight, BorderLayout.EAST);
	    
		southPanel.add(currentPanel);
	}
	
	/**
	 * Update fields with the given character's infos.
	 * @param p_entity
	 */
	public void focusPerso(Perso p_perso) {
		updatingUI=true;	// To disable the listeners
		
		name.setText(p_perso.getNom());
		script.setSelectedIndex(p_perso.getQuel_deplacement().valeur);
		angle.setSelectedIndex(p_perso.getAngle().value);
		info.setSelectedIndex(p_perso.getInfo().ordinal());
		object.setText("0");

		currentPerso=p_perso;
	    MapDialog mapDialog=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
	    behavior=mapDialog.getBehaviors().get(currentPerso.getNom());
		updateDialog();
		
		// Initialize the spinner
		initSpinner();
		
		updatingUI=false;
	}
	
	private void initSpinner() {
		int max=0;
		if (behavior != null) {
			for (int i=0;i<behavior.replique.length;i++) {
				if (behavior.replique[i] == 0) {
					max=i;
				}
			}
		}
		spinner.setModel(new SpinnerNumberModel(0, 0, max, -1));	// -1 to get next sentence with down arrow
	}
	
	/**
	 * Update the dialog's textarea, according to the current Perso and the behavior (spinner's position)
	 */
	private void updateDialog() {
	    MapDialog mapDialog=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
	    int val=(Integer) spinner.getValue();
	    String dial=mapDialog.getSentence(behavior, val);
	    dialogZone.setText(dial);
	    dialogZone.setCaretPosition(0);
	}
	
	/**
	 * Listener for all widgets in the "south panel" : name, angle, script, info and dialog.<br/>
	 * Manages the synchronisation between UI and model (Perso class)
	 * @author Tchegito
	 *
	 */
	class PersoWidgetListener implements ActionListener, DocumentListener, ChangeListener {

		public void actionPerformed(ActionEvent actionevent) {
			if (!updatingUI) {
				Component comp=(Component) actionevent.getSource();
				if (comp == angle || comp == script || comp == info) {
					String val=(String) ((JComboBox) comp).getSelectedItem();
					if (comp==angle) {
						Angle a=ZUtils.getField(val, Angle.class);
						currentPerso.setAngle(a);
					} else if (comp == script) {
						MouvementPerso s=ZUtils.getField(val, MouvementPerso.class);
						currentPerso.setQuel_deplacement(s);
					} else if (comp == info) {
						PersoInfo i=ZUtils.getField(val, PersoInfo.class);
						currentPerso.setInfo(i);
					}
				}
				manager.getZildoCanvas().setChangeSprites(true);
			}
		}

		public void changedUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}

		public void removeUpdate(DocumentEvent documentevent) {
			updateText(documentevent);		
		}

		public void insertUpdate(DocumentEvent documentevent) {
			updateText(documentevent);
		}
		
		@Override
		public void stateChanged(ChangeEvent changeevent) {
			updateDialog();
		}
		
		private void updateText(DocumentEvent p_event) {
			if (!updatingUI) {
				Component comp=KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
				Document doc=p_event.getDocument();
		    	try {
					String txt=doc.getText(0, doc.getLength());
					if (comp == dialogZone) {
						updatePersoText(txt);
					} else if (comp == name) {
						updatePersoName(txt);
					}
		    	} catch (BadLocationException e) {
		    		
		    	}
			}
		}
		
		private void updatePersoText(String p_text) {
			Area area=EngineZildo.mapManagement.getCurrentMap();
			MapDialog dialogs=area.getMapDialog();
		    if (behavior != null) {
	    		dialogs.setSentence(behavior, (Integer) spinner.getValue(), p_text);
		    }
		}
		
		private void updatePersoName(String p_text) {
			currentPerso.setNom(p_text);
		}
	}
}
