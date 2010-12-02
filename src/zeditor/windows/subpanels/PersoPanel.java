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
import java.awt.GridLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import zeditor.core.tiles.PersoSet;
import zeditor.windows.managers.MasterFrameManager;
import zildo.fwk.ZUtils;
import zildo.monde.map.Angle;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.Perso.PersoInfo;
import zildo.monde.sprites.utils.MouvementPerso;

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
	}
}
