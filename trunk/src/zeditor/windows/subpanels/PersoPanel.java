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
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;

import zeditor.core.tiles.PersoSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.Behavior;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Angle;
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
	
	public PersoPanel(MasterFrameManager p_manager) {
		setLayout(new BorderLayout());
		add(new PersoSet(null, p_manager), BorderLayout.CENTER);
		add(getSouthPanel(), BorderLayout.SOUTH);

		manager=p_manager;
	}
	
	private JPanel getSouthPanel() {
		JPanel panel=new JPanel();
		GridLayout thisLayout = new GridLayout(0,2);
		panel.setLayout(thisLayout);

		name=new JTextField();
		panel.add(new JLabel("Nom"));
		panel.add(name);
		panel.add(new JLabel("Script"));
		script=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(MouvementPerso.class)));
		panel.add(script);
		
		panel.add(new JLabel("Angle"));
		angle=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(Angle.class)));
		panel.add(angle);

		panel.add(new JLabel("Objet"));
		object=new JTextField();
		panel.add(object);
		
		panel.add(new JLabel("Info"));
		info=new JComboBox(new DefaultComboBoxModel(ZUtils.getValues(PersoInfo.class)));
		panel.add(info);
		
		// Spinner for the dialogs
		SpinnerListModel dialogModel = new SpinnerListModel(new String[] {"0", "1", "2"});
		spinner = new JSpinner(dialogModel);
		spinner.setPreferredSize(new Dimension(40,12));
		JPanel subPanel=new JPanel();
		subPanel.setLayout(new BorderLayout());
		subPanel.add(new JLabel("Dialog"), BorderLayout.WEST);
		subPanel.add(spinner, BorderLayout.EAST);
		panel.add(subPanel);
		
		dialogZone=new JTextArea(4, 30);
		dialogZone.setLineWrap(true);
		dialogZone.setWrapStyleWord(true);
		dialogZone.setAutoscrolls(true);
		dialogZone.setMaximumSize(dialogZone.getSize());
		panel.add(dialogZone);

		return panel;
	}
	
	/**
	 * Update fields with the given character's infos.
	 * @param p_entity
	 */
	public void focusPerso(Perso p_perso) {
		name.setText(p_perso.getNom());
		script.setSelectedIndex(p_perso.getQuel_deplacement().valeur);
		angle.setSelectedIndex(p_perso.getAngle().value);
		info.setSelectedIndex(p_perso.getInfo().ordinal());
		object.setText("0");
		
		currentPerso=p_perso;
		updateDialog();
	}
	
	public void updateDialog() {
	    MapDialog mapDialog=EngineZildo.mapManagement.getCurrentMap().getMapDialog();
	    Behavior behav=mapDialog.getBehaviors().get(currentPerso.getNom());
	    String dial=mapDialog.getSentence(behav, currentPerso.getCompte_dialogue());
	    dialogZone.setText(dial);

	}
}
