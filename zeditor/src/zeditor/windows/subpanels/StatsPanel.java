package zeditor.windows.subpanels;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zeditor.windows.dialogs.ChangeOriginDialog;
import zeditor.windows.managers.MasterFrameManager;
import zildo.client.sound.Ambient.Atmosphere;
import zildo.fwk.ZUtils;
import zildo.monde.dialog.MapDialog;
import zildo.monde.map.Area;
import zildo.monde.sprites.persos.Perso;
import zildo.server.EngineZildo;

@SuppressWarnings("serial")
public class StatsPanel extends JPanel {

	MasterFrameManager manager;
	
	JLabel dim;
	JLabel nSpr;
	JLabel nPerso;
	JLabel nChainingPoint;
	JLabel nDialogs;
	JLabel nFloors;
	JSpinner spinLimitX, spinLimitY;
	JSpinner spinOffsetX, spinOffsetY;
	JComboBox atmosphere;

	boolean updatingUI; // To know wether user or UI ask for update

	public StatsPanel(MasterFrameManager manager) {
		this.manager = manager;
		setLayout(new GridLayout(12, 2));

		add(new JLabel("Dimension"));
		add(dim = new JLabel(""));
		add(new JLabel("Floors"));
		add(nFloors = new JLabel(""));
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

		spinOffsetX = new JSpinner(new SpinnerNumberModel(0, 0, 64, -1));
		spinOffsetY = new JSpinner(new SpinnerNumberModel(0, 0, 64, -1));

		add(new JLabel("Offset X"));
		add(spinOffsetX);
		add(new JLabel("Offset Y"));
		add(spinOffsetY);

		add(new JLabel(""));
		add(new JButton(new AbstractAction("Change origin") {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog = new ChangeOriginDialog();
				dialog.setLocationRelativeTo(null);
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		}));
		ChangeListener listener = new StatsFieldsListener();
		spinLimitX.addChangeListener(listener);
		spinLimitY.addChangeListener(listener);
		spinOffsetX.addChangeListener(listener);
		spinOffsetY.addChangeListener(listener);
		atmosphere.addActionListener((ActionListener) listener);
	}

	public void updateStats() {
		updatingUI = true;
		Area map = EngineZildo.mapManagement.getCurrentMap();
		int nbPerso = EngineZildo.persoManagement.tab_perso.size();
		int nbSpr = EngineZildo.spriteManagement.getSpriteEntities(null).size();
		if (map != null) {
			int nbChPoint = map.getChainingPoints().size();
			MapDialog dialogs = map.getMapDialog();
			int nbDial = dialogs == null ? 0 : dialogs.getN_phrases();
			dim.setText(map.getDim_x() + " x " + map.getDim_y());
			nFloors.setText("" + (map.getHighestFloor()+1));
			nPerso.setText(String.valueOf(nbPerso));
			nPerso.setToolTipText( "<html>" + getPersoNames() + "</html>");
			nSpr.setText(String.valueOf(nbSpr - nbPerso));
			nChainingPoint.setText(String.valueOf(nbChPoint));
			nDialogs.setText(String.valueOf(nbDial));
	
			atmosphere.setSelectedIndex(map.getAtmosphere().ordinal());
			spinLimitX.setValue(map.getDim_x());
			spinLimitY.setValue(map.getDim_y());
			spinOffsetX.setValue(map.getScrollOffset().x);
			spinOffsetY.setValue(map.getScrollOffset().y);
		}
		updatingUI = false;
	}

	private String getPersoNames() {
		StringBuilder sb = new StringBuilder();
		for (Perso p : EngineZildo.persoManagement.tab_perso) {
			sb.append(p.getDesc());
			sb.append(" (").append(p.getName()).append("-").append(p.getX()).append(",").append(p.getY()).append("),<br/>");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
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
			if (!updatingUI) {
				// If we are focusing on an existing sprite, then update his
				// attributes
				Component comp = (Component) changeevent.getSource();
				if (comp instanceof JSpinner) {
					int val = (Integer) ((JSpinner) comp).getValue();
					Area map = EngineZildo.mapManagement.getCurrentMap();
					if (comp == spinLimitX) {
						map.setDim_x(val);
					} else if (comp == spinLimitY) {
						map.setDim_y(val);
					} else if (comp == spinOffsetX) {
						map.getScrollOffset().x = val;
					} else if (comp == spinOffsetY) {
						map.getScrollOffset().y = val;
					}
					manager.setUnsavedChanges(true);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent changeevent) {
			if (!updatingUI) {
				Component comp = (Component) changeevent.getSource();
				Area map = EngineZildo.mapManagement.getCurrentMap();
				if (comp == atmosphere) {
					String val = (String) ((JComboBox) comp).getSelectedItem();
					Atmosphere a = ZUtils.getField(val, Atmosphere.class);
					map.setAtmosphere(a);
					manager.setUnsavedChanges(true);
				}
			}
		}
	}
}
