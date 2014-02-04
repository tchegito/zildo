package zeditor.windows.subpanels;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.server.EngineZildo;

@SuppressWarnings("serial")
public class StatsPanel extends JPanel {

	JLabel dim;
	JLabel nSpr;
	JLabel nPerso;
	JLabel nChainingPoint;
	JLabel nDialogs;
	JSpinner spinLimitX;
	JSpinner spinLimitY;
	JComboBox atmosphere;

	public StatsPanel() {
		setLayout(new GridLayout(8, 2));

		add(new JLabel("Dimension"));
		add(dim = new JLabel(""));
		add(new JLabel("Sprites"));
		add(nSpr = new JLabel(""));
		add(new JLabel("Personnages"));
		add(nPerso = new JLabel(""));
		add(new JLabel("Points d'enchainement"));
		add(nChainingPoint = new JLabel(""));
		add(new JLabel("Dialogues"));
		add(nDialogs = new JLabel(""));

		atmosphere = new JComboBox(ZUtils.getValues(Atmosphere.class));
		add(new JLabel("Atmosphere"));
		add(atmosphere);

		spinLimitX = new JSpinner(new SpinnerNumberModel(1, 1, 64, -1));
		spinLimitY = new JSpinner(new SpinnerNumberModel(1, 1, 64, -1));

		add(new JLabel("Taille X"));
		add(spinLimitX);

		add(new JLabel("Taille Y"));
		add(spinLimitY);

		ChangeListener listener = new StatsFieldsListener();
		spinLimitX.addChangeListener(listener);
		spinLimitY.addChangeListener(listener);
		atmosphere.addActionListener((ActionListener) listener);
	}

	public void updateStats() {
		Area map = EngineZildo.getMapManagement().getCurrentMap();
		int nbPerso = EngineZildo.persoManagement.tab_perso.size();
		int nbSpr = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		int nbChPoint = map.getChainingPoints().size();
		MapDialog dialogs = map.getMapDialog();
		int nbDial = dialogs == null ? 0 : dialogs.getN_phrases();
		dim.setText(map.getDim_x() + " x " + map.getDim_y());
		nPerso.setText(String.valueOf(nbPerso));
		nSpr.setText(String.valueOf(nbSpr - nbPerso));
		nChainingPoint.setText(String.valueOf(nbChPoint));
		nDialogs.setText(String.valueOf(nbDial));

		atmosphere.setSelectedIndex(map.getAtmosphere().ordinal());
		spinLimitX.setValue(map.getDim_x());
		spinLimitY.setValue(map.getDim_y());
	}

	@Override
	public void setVisible(boolean p_flag) {
		if (p_flag) {
			updateStats();
		}
		super.setVisible(p_flag);
	}

	class StatsFieldsListener implements ChangeListener, ActionListener {

		@Override
		public void stateChanged(ChangeEvent changeevent) {

			// If we are focusing on an existing sprite, then update his
			// attributes
			Component comp = (Component) changeevent.getSource();
			if (comp instanceof JSpinner) {
				int val = (Integer) ((JSpinner) comp).getValue();
				Area map = EngineZildo.getMapManagement().getCurrentMap();
				if (comp == spinLimitX) {
					map.setDim_x(val);
				} else if (comp == spinLimitY) {
					map.setDim_y(val);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent changeevent) {
			Component comp = (Component) changeevent.getSource();
			Area map = EngineZildo.getMapManagement().getCurrentMap();
			if (comp == atmosphere) {
				String val = (String) ((JComboBox) comp).getSelectedItem();
				Atmosphere a = ZUtils.getField(val, Atmosphere.class);
				map.setAtmosphere(a);
			}
		}
	}
}
